package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.io.InputStream;
import java.util.Properties;

public class App {

    // Replace with your YouTube API key
    private static String API_KEY;  
    
    // Replace with the Playlist ID you want to extract titles from
    private static final String PLAYLIST_ID = "PLbpi6ZahtOH4wB_j_xc_ks8GbPLjz15eO&ab";  

    public static List<String> getTitles() {        
        
        
        List<String> titles = new ArrayList<>();
        
        try {
            // Get API key from config.properties
            loadApiKey();
            // Build the YouTube Data API request URL
            String urlString = "https://www.googleapis.com/youtube/v3/playlistItems" +
                    "?part=snippet" +
                    "&playlistId=" + PLAYLIST_ID +
                    "&maxResults=50" +
                    "&key=" + API_KEY;
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // Check if the request was successful
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error code: " + conn.getResponseCode());
            }

            // Read the response from the API
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            // Close the connection
            conn.disconnect();

            // Parse the JSON response using Gson
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(sb.toString(), JsonObject.class);

            // Extract the items array
            JsonArray items = jsonResponse.getAsJsonArray("items");

            // Loop through the items and print the video titles
            for (int i = 0; i < items.size(); i++) {
                JsonObject snippet = items.get(i).getAsJsonObject()
                        .getAsJsonObject("snippet");
                String title = snippet.get("title").getAsString();
                titles.add(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return titles;
    }

    private static void loadApiKey() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new Exception("Unable to find config.properties");
            }
            properties.load(input);
            API_KEY = properties.getProperty("youtube.api.key");
        }
    }

    public static void main(String[] args) {
        List<String> titles = getTitles();

        for (String t : titles) {
            System.out.println(t);
        }
    }
}
