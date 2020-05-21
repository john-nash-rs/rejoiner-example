package com.nulpointerexception.server;

import com.google.api.graphql.rejoiner.Query;
import com.google.api.graphql.rejoiner.SchemaModification;
import com.google.api.graphql.rejoiner.SchemaModule;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.nulpointerexception.server.proto.BookingNexusServiceDemo;
import com.nulpointerexception.server.proto.SignUpServiceGrpc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class GRPCAppSchemaModule extends SchemaModule {

    @Inject
    SignUpServiceGrpc.SignUpServiceFutureStub client;

    @Query("weather")
    String getWeather() throws InterruptedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("================= At weather service start============"+dtf.format(now));
       // Thread.sleep(8000);
        now = LocalDateTime.now();
        System.out.println("================= At weather service stop============"+dtf.format(now));
        return "temperature is 32 degree celcius";
    }
    @Query("rewards")
    BookingNexusServiceDemo.SignUpResponse reward(BookingNexusServiceDemo.UserData request, SignUpServiceGrpc.SignUpServiceBlockingStub client) {
        return client.signUp(request);
    }

    @Query("rewardAsync")
    ListenableFuture<BookingNexusServiceDemo.SignUpResponse> rewardAsync(BookingNexusServiceDemo.UserData request) {
        return client.signUp(request);
    }

    @Query("combine")
    String getCombineData(BookingNexusServiceDemo.UserData request) throws InterruptedException, ExecutionException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("================= At Combine service start============"+dtf.format(now));
        ListenableFuture<BookingNexusServiceDemo.SignUpResponse> response = client.signUp(request);
        String weatherResult = getWeather();
        String reward = response.get().getResult();
        now = LocalDateTime.now();
        System.out.println("================= At Combine service stop============"+dtf.format(now));
        return weatherResult +" "+reward;
    }

    @SchemaModification(addField = "farm", onType = BookingNexusServiceDemo.SignUpResponse.class)
    String farm(BookingNexusServiceDemo.SignUpResponse response) {
        return response.getResult() +" has 2 Acres";
    }



}
