package com.nulpointerexception.server;


import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.util.logging.Logger;

public class Application {
    private static final int HTTP_PORT = 8080;
    private static final Logger logger = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {
        Server server = new Server(HTTP_PORT);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setWelcomeFiles(new String[] {"index.html"});
        resourceHandler.setDirectoriesListed(true);
        // resource base is relative to the WORKSPACE file
        resourceHandler.setResourceBase("./src/main/resources");
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(
                new Handler[] {resourceHandler, new GraphQlHandler(), new DefaultHandler()});
        server.setHandler(handlerList);
        server.start();
        logger.info("Server running on port " + HTTP_PORT);
        server.join();
    }
}
