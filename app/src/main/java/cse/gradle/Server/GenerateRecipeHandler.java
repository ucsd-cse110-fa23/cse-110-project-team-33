package cse.gradle.Server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;
import org.json.JSONArray;

import com.google.common.io.Files;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import cse.gradle.Recipe;
import cse.gradle.RecipeGenerator;
import cse.gradle.Server.APIs.ChatGPTApiClient;
import cse.gradle.Server.APIs.WhisperApiClient;

public class GenerateRecipeHandler implements HttpHandler {

    MongoDB usersDB;

    public GenerateRecipeHandler(MongoDB mongoDB) {
        this.usersDB = mongoDB;
        usersDB.connect();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
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
        System.out.println("created: " + FILE_TO_RECEIVED + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        if (!file.exists()) {
            file.createNewFile();
        }
        
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
        
        httpExchange.sendResponseHeaders(200, 0);
    }

    // helper method for handle()
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

    // private String handlePost(HttpExchange httpExchange) throws IOException {

    //     // decode audio file

    //     // get mealType and ingredients from whisper
    //     String mealType = "";
    //     String ingredients = "";

    //     String[] transcriptions = new String[2];

    //     WhisperApiClient whisperApi = new WhisperApiClient();
    //     transcriptions[0] = whisperApi.generateResponse(mealTypeFilePath);
    //     transcriptions[1] = whisperApi.generateResponse(ingredientsFilePath);

    //     // use transcriptions to generate recipe
    //     String[] response = new String[4];

    //     try {
    //         ChatGPTApiClient chatGPTApi = new ChatGPTApiClient();
    //         response = chatGPTApi.generateResponse(mealType, ingredients);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     // JSONify recipe
    //     Recipe newRecipe = new Recipe(response[2], response[3], response[1], response[0]);
    //     String jsonRecipe = newRecipe.toDocument().toJson();
        
    //     return jsonRecipe;
    // }

}
