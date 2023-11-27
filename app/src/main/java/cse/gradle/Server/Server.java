package cse.gradle.Server;

import com.sun.net.httpserver.*;

import cse.gradle.Recipe;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Server {
    public static final int SERVER_PORT = 8100;
    public static final String SERVER_HOSTNAME = "localhost";

    public Server() throws IOException {
        // create a thread pool to handle requests
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        // Initialize a hashmap to store our data
        Map<String, Recipe> data = new HashMap<>();

        // Create a server
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT), 0);

        // Create a context for the server at root path "/" and associate it with
        // HttpHandler object "RequestHandler"
        server.createContext("/", new RequestHandler(data));
        // Set the server's executor object to be threadPoolExecutor
        server.setExecutor(threadPoolExecutor);
        // Start the server
        server.start();

        System.out.println("Server started at " + SERVER_HOSTNAME + ":" + SERVER_PORT);
    }

}
