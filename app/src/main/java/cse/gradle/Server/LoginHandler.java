package cse.gradle.Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cse.gradle.Recipe;
import cse.gradle.User;

import java.io.*;
import java.util.*;

import org.bson.Document;

public class LoginHandler implements HttpHandler {
    // Handles a POST request for user login

    MongoDB usersDB;

    public LoginHandler(MongoDB mongoDB) {
        this.usersDB = mongoDB;
        usersDB.connect();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "Request Received";
        String method = httpExchange.getRequestMethod();
        try {
            if (method.equals("POST")) {
                response = handleGet(httpExchange);
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
    // Handles a GET request for a new user login
    // Accepted JSON Format: {"username": "username", "password": "password"}
    private String handleGet(HttpExchange httpExchange) throws IOException {

        String response = "Invalid GET request: ";

        try {

            InputStream inStream = httpExchange.getRequestBody();
            Scanner scanner = new Scanner(inStream);
            StringBuilder getData = new StringBuilder();

            while (scanner.hasNextLine()) {
                getData.append(scanner.nextLine());
            }

            scanner.close();
            inStream.close();

            System.out.println("Received JSON data: " + getData);

            // Parse the JSON request body into a JsonNode tree
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(getData.toString());

            // Get the username and password from the request body
            String username = root.get("username").asText();
            String password = root.get("password").asText();

            // Check if username password pair is in the database
            Document user = usersDB.findOne("username", username);
            if (user != null) {
                // If the user exists, check if the password is correct
                if (user.get("password").equals(password)) {
                    // If the password is correct, return the user's id 
                    return user.get("userId").toString();
                } else {
                    // If the password is incorrect, return an error message
                    response += "Incorrect password for user " + username;
                    throw new Exception(response);
                }
            } else {
                // If the user does not exist, return an error message
                response += "User " + username + " does not exist";
                throw new Exception(response);
            }
        } catch (Exception e) {
            // If an exception is thrown, return an error message
            e.printStackTrace();
        }
        return response;
    } 


}