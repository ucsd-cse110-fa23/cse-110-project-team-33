package cse.gradle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;


public class Model {
    /*
     * Performs an HTTP request to the server
     * For GET requests, uid should be the id of the recipe to retrieve
     * For POST requests, uid should be null and recipe should be the recipe to add
     * For PUT requests, uid should be the id of the recipe to update and recipe should be the updated recipe
     * For DELETE requests, uid should be the id of the recipe to delete and recipe should be null
     */
      public String performRequest(String method, String uid, Recipe recipe) {
        try {
            String urlString = "http://localhost:8100/";
            if (uid != null) {
                urlString += ("?=" + uid);
            }
            URL url = new URI(urlString).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);

            if (method.equals("POST") || method.equals("PUT")) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonRecipe = objectMapper.writeValueAsString(recipe);

                try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream())) {
                    out.write(jsonRecipe);
                }
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error: " + ex.getMessage();
        }
    }
}