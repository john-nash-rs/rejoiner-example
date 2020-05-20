package com.nulpointerexception.server;

import com.google.api.graphql.execution.GuavaListenableFutureSupport;
import com.google.api.graphql.rejoiner.Schema;
import com.google.api.graphql.rejoiner.SchemaProviderModule;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Key;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLSchema;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GraphQlHandler extends AbstractHandler {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final TypeToken<Map<String, Object>> MAP_TYPE_TOKEN =
            new TypeToken<Map<String, Object>>() {};

    private static final GraphQLSchema SCHEMA =
            Guice.createInjector(new SchemaProviderModule(), new GRPCAppClientModule(), new GRPCAppSchemaModule())
                    .getInstance(Key.get(GraphQLSchema.class, Schema.class));

    Instrumentation instrumentation =
            new ChainedInstrumentation(
                    Arrays.asList(
                            GuavaListenableFutureSupport.listenableFutureInstrumentation()));
    GraphQL GRAPHQL = GraphQL.newGraphQL(SCHEMA).instrumentation(instrumentation).build();

    public void handle(String url, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        if ("/graphql".equals(url)) {
            request.setHandled(true);
            Map<String, Object> json = readJson(httpServletRequest);
            String query = (String) json.get("query");
            if (query == null) {
                httpServletResponse.setStatus(400);
                return;
            }
            String operationName = (String) json.get("operationName");
            Map<String, Object> variables = getVariables(json.get("variables"));

            ExecutionInput executionInput =
                    ExecutionInput.newExecutionInput()
                            .query(query)
                            .operationName(operationName)
                            .variables(variables)
                            .context(new Object())
                            .build();
            ExecutionResult executionResult = GRAPHQL.execute(executionInput);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            GSON.toJson(executionResult.toSpecification(), httpServletResponse.getWriter());
        }
    }

    private static Map<String, Object> getVariables(Object variables) {
        Map<String, Object> variablesWithStringKey = new HashMap<>();
        if (variables instanceof Map) {
            ((Map) variables).forEach((k, v) -> variablesWithStringKey.put(String.valueOf(k), v));
        }
        return variablesWithStringKey;
    }

    private static Map<String, Object> readJson(HttpServletRequest request) {
        try {
            String json = CharStreams.toString(request.getReader());
            return jsonToMap(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Object> jsonToMap(String json) {
        if (Strings.isNullOrEmpty(json)) {
            return ImmutableMap.of();
        }
        return Optional.<Map<String, Object>>ofNullable(GSON.fromJson(json, MAP_TYPE_TOKEN.getType()))
                .orElse(ImmutableMap.of());
    }
}
