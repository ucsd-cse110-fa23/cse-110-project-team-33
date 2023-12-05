package cse.gradle.Server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import cse.gradle.Recipe;
import cse.gradle.Server.APIs.ChatGPTApiClient;
import cse.gradle.Server.APIs.WhisperApiClient;

public class GenerateRecipeHandler implements HttpHandler {

    MongoDB usersDB;

    public GenerateRecipeHandler(MongoDB mongoDB) {
        this.usersDB = mongoDB;
        usersDB.connect();
    }

    // receive request from Model and execute handlePut or handlePost accordingly
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "Request Received";
        String method = httpExchange.getRequestMethod();
        try {
            if (method.equals("PUT")) {
                response = handlePut(httpExchange);
            } else if (method.equals("POST")) {
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

    // TODO: test needed
    // use the mealType.wav and ingredients.wav stored on the server, 
    // make Whisper and ChatGPT api calls to generate the recipe, 
    // convert the responses to a JSON recipe, and send the JSON recipe to Model.
    private String handlePost(HttpExchange httpExchange) throws IOException, URISyntaxException {
        // get mealType and ingredients from whisper
        String mealTypeFilePath = "src/main/java/cse/gradle/Server/mealType.wav";
        String ingredientsFilePath = "src/main/java/cse/gradle/Server/ingredients.wav";

        WhisperApiClient whisperApi = new WhisperApiClient();
        String mealType = whisperApi.generateResponse(mealTypeFilePath);
        String ingredients = whisperApi.generateResponse(ingredientsFilePath);

        // use transcriptions to generate recipe
        String[] response = new String[4];

        try {
            ChatGPTApiClient chatGPTApi = new ChatGPTApiClient();
            response = chatGPTApi.generateResponse(mealType, ingredients);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // JSONify recipe
        Recipe newRecipe = new Recipe(response[2], response[3], response[1], response[0]);
        String jsonRecipe = newRecipe.toDocument().toJson();
        
        return jsonRecipe;
    }

    // TODO: test needed
    // decode and store the audio file received from Model
    private String handlePut(HttpExchange httpExchange) throws IOException {
        String CRLF = "\r\n";
        int fileSize = 0;

        URI uri = httpExchange.getRequestURI();
        System.out.println("URI: " + uri);
        String query = uri.getRawQuery();
        System.out.println("Query: " + query);
        String audioFile = query.substring(query.indexOf("=") + 1);
        System.out.println("audioFile parsed from URL: " + audioFile);
        
        String FILE_TO_RECEIVED = "src/main/java/cse/gradle/Server/" + audioFile;
        File file = new File(FILE_TO_RECEIVED);
        System.out.println("audio file stored at: " + FILE_TO_RECEIVED);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            InputStream input = httpExchange.getRequestBody();
            String nextLine = "";
            do {
                nextLine = readLine(input, CRLF);
                if (nextLine.startsWith("Content-Length:")) {
                    fileSize = 
                        Integer.parseInt(
                            nextLine.replaceAll(" ", "").substring(
                                "Content-Length:".length()
                            )
                        );
                }
                System.out.println(nextLine);
            } while (!nextLine.equals(""));
            
            byte[] midFileByteArray = new byte[fileSize];
            int readOffset = 0;
            while (readOffset < fileSize) {
                int bytesRead = input.read(midFileByteArray, readOffset, fileSize);
                readOffset += bytesRead;
            }
            
            BufferedOutputStream bos = 
                new BufferedOutputStream(new FileOutputStream(FILE_TO_RECEIVED));
            
            bos.write(midFileByteArray, 0, fileSize);
            bos.flush();
            bos.close();
            return "File uploaded successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading file: " + e.getMessage();
        }
    }

    // helper method for handlePut()
    private static String readLine(InputStream is, String lineSeparator) 
        throws IOException {

        int off = 0, i = 0;
        byte[] separator = lineSeparator.getBytes("UTF-8");
        byte[] lineBytes = new byte[1024];
        
        while (is.available() > 0) {
            int nextByte = is.read();
            if (nextByte < -1) {
                throw new IOException(
                    "Reached end of stream while reading the current line!");
            }
            
            lineBytes[i] = (byte) nextByte;
            if (lineBytes[i++] == separator[off++]) {
                if (off == separator.length) {
                    return new String(
                        lineBytes, 0, i-separator.length, "UTF-8");
                }
            }
            else {
                off = 0;
            }
            
            if (i == lineBytes.length) {
                throw new IOException("Maximum line length exceeded: " + i);
            }
        }
        
        throw new IOException(
            "Reached end of stream while reading the current line!");       
    }
}
