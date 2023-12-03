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
    public static HttpServer server;


    public static void startServer() throws IOException {
        // create a thread pool to handle requests
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        // Create a server
        server = HttpServer.create(new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT), 0);


        // Create a MongoDB object that accesses the users colleciton in the user_db database
        MongoDB usersDb = new MongoDB(
            "mongodb+srv://trevor:cse110@dev-azure-desktop.4j6hron.mongodb.net/?retryWrites=true&w=majority",
                "user_db", "users");

        // Register the handlers
        server.createContext("/recipe", new RecipeHandler(usersDb));
        server.createContext("/login", new LoginHandler(usersDb));
        server.createContext("/register", new RegisterHandler(usersDb));

        // Set the server's executor object to be threadPoolExecutor
        server.setExecutor(threadPoolExecutor);
        // Start the server
        server.start();

        System.out.println("Server started at " + SERVER_HOSTNAME + ":" + SERVER_PORT);
    }

    public static void stopServer() {
        server.stop(0);
    }

}
