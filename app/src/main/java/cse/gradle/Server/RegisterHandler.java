package cse.gradle.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.User;

import java.io.*;
import java.util.*;

public class RegisterHandler implements HttpHandler {

    MongoDB usersDB;

    public RegisterHandler(MongoDB mongoDB) {
        this.usersDB = mongoDB;
        usersDB.connect();
    }

    // Handles a POST request for a new user registration
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "Request Received";
        String method = httpExchange.getRequestMethod();
        try {
            if (method.equals("POST")) {
                response = handlePost(httpExchange);
            } else {
                throw new Exception("Unsupported HTTP method: " + method);
            }

            // Set the response character encoding to UTF-8
            httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");

            // Convert the response string to bytes with UTF-8 encoding
            byte[] responseBytes = response.getBytes("UTF-8");

            // Send the response
            httpExchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpExchange.sendResponseHeaders(500, response.length());
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // TODO: Write a test for this method
    // Handles a POST request for a new user registration
    // Accepted JSON Format: {"username": "username", "password": "password"}
    private String handlePost(HttpExchange httpExchange) throws IOException {
        String response = "Invalid POST request";
        try {
            InputStream inStream = httpExchange.getRequestBody();
            Scanner scanner = new Scanner(inStream);
            StringBuilder postData = new StringBuilder();

            while (scanner.hasNextLine()) {
                postData.append(scanner.nextLine());
            }

            scanner.close();
            inStream.close();

            System.out.println("Received JSON data: " + postData);

            // Parse the JSON request body into a JsonNode tree
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(postData.toString());

            // Get the username and password from the request body
            String username = root.get("username").asText();
            String password = root.get("password").asText();

            if (usersDB.findOne("username", username) != null) {
                response += "Error: Username " + username + " already exists";
                throw new Exception(response);
            }

            // Create a new User object using the username and password and
            // insert it into the database. Intially, the user will have no
            // recipes and the constructor creates a UUID for the user
            User user = new User(username, password);
            usersDB.insertOne(user.toDocument());

            // return the userId of the newly created user
            response = user.getUserId().toString();

        } catch (Exception e) {
            // If an exception is thrown, return an error message
            e.printStackTrace();
        }

        return response;
    }

}
