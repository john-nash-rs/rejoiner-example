package com.nulpointerexception.server;


import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.logging.Logger;

public class Application {
    private static final int HTTP_PORT = 8080;
    private static final Logger logger = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {
        Server server = new Server(HTTP_PORT);

        ServletContextHandler context = new ServletContextHandler();
// Set the context for all filters and servlets
// Required for the internal servlet & filter ServletContext to be sane
        context.setContextPath("/");
// The servlet context is what holds the welcome list
// (not the ResourceHandler or DefaultServlet)
        context.setWelcomeFiles(new String[] { "index.html" });

// Add a servlet
        //context.addServlet(ServerPageRoot.class,"/servlet/*");

// Add the filter, and then use the provided FilterHolder to configure it
        FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

// Use a DefaultServlet to serve static files.
// Alternate Holder technique, prepare then add.
// DefaultServlet should be named 'default'
        ServletHolder def = new ServletHolder("default", DefaultServlet.class);
        def.setInitParameter("resourceBase","./src/main/resources");
        def.setInitParameter("dirAllowed","false");
        context.addServlet(def,"/");

        ServletHolder graphQl = new ServletHolder("graphql", GraphQlHandlerServlet.class);
        def.setInitParameter("resourceBase","./http/");
        def.setInitParameter("dirAllowed","false");
        context.addServlet(graphQl,"/graphql");





        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setWelcomeFiles(new String[] {"index.html"});
        resourceHandler.setDirectoriesListed(true);
        // resource base is relative to the WORKSPACE file
        resourceHandler.setResourceBase("./src/main/resources");
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(
                new Handler[] {context, resourceHandler, new GraphQlHandler(), new DefaultHandler()});
        server.setHandler(handlerList);
        server.start();
        logger.info("Server running on port " + HTTP_PORT);
        server.join();
    }
}
