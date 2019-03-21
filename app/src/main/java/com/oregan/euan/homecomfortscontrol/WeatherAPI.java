package com.oregan.euan.homecomfortscontrol;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

 class WeatherAPI {

     private static String
             api_url =
             "http://api.openweathermap.org/data/2.5/weather?lat=51.895548" +
                     "&lon=-8.489198&units=metric&APPID=fb59b4d9d9a9c3fa5ce7747f8c17a780";

    @NonNull
    private String requestURL(String url) throws IOException {
        String outputJSON;

        URL requestFrom = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) requestFrom.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuffer response = new StringBuffer();
        while ((outputJSON = in.readLine()) != null) {
            response.append(outputJSON);
        } in.close();

        return response.toString();
    }

     String request(String query){
         String result = null;
         try {
             String outputJSON = requestURL(api_url);
             JsonObject jsonObject = new JsonParser().parse(outputJSON).getAsJsonObject();
             result = jsonObject.getAsJsonObject().get(query).getAsString();

         } catch (IOException e) {
             e.printStackTrace();
         }
         return result;
     }

     String requestMain(String query){
        String result = null;
        try {
            String outputJSON = requestURL(api_url);
            JsonObject jsonObject = new JsonParser().parse(outputJSON).getAsJsonObject();
            result = jsonObject.getAsJsonObject("main").get(query).getAsString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

     String requestWeather(String query){
         String result = null;
         try {
             String outputJSON = requestURL(api_url);
             JsonArray jsonArray = new JsonParser().parse(outputJSON).getAsJsonObject().getAsJsonArray("weather");
             result = jsonArray.getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString();

         } catch (IOException e) {
             e.printStackTrace();
         }
         return result;
     }
}
