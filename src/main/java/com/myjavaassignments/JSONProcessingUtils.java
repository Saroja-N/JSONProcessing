package com.myjavaassignments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class JSONProcessingUtils {

    private static final String PROPS_FILE_NAME = "application.properties";
    private static final String JSON_ATTR_NAME = "numbers";

    private static String getUrl() {
        String url = "";
        Properties properties = new Properties();
        try (InputStream inputStream = JSONProcessingUtils.class.getClassLoader().getResourceAsStream(PROPS_FILE_NAME)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
            url = properties.getProperty("url");
        } catch (IOException | NullPointerException e) {
            System.out.println("Problem fetching url from application properties, exception occured is - " + e);
        }
        return url;
    }

    private JSONArray getResponse() {

        JSONArray responseJSONArray = new JSONArray();
        StringBuilder response;
        URL url;
        HttpURLConnection httpURLConnection;
        try {
            url = new URL(getUrl());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // Default Request Method for a HttpURLConnection entity is, GET.
            // Hence skipped explicit set request method to GET
        } catch (IOException e) {
            System.out.println("Problematic URL");
            return responseJSONArray;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            JSONObject jsonObject = new JSONObject(response.toString());
            responseJSONArray = jsonObject.getJSONArray(JSON_ATTR_NAME);
        } catch (UnsupportedOperationException | IOException | JSONException e) {
            System.out.println("Problem fetching JSON response");
        }
        return responseJSONArray;
    }

    private void processJSONAndPrintResult(JSONArray jsonArray) {

        int resultSum = 0;
        JSONObject tempJSONObject;
        JSONArray tempJSONArray;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                tempJSONObject = jsonArray.getJSONObject(i);
                tempJSONArray = tempJSONObject.getJSONArray(JSON_ATTR_NAME);
                int currentSumOfNumbersArray = 0;
                for (int j = 0; j < tempJSONArray.length(); j++) {
                    currentSumOfNumbersArray += tempJSONArray.getInt(j);
                }
                System.out.println("Sum of current array :" + currentSumOfNumbersArray);
                resultSum += currentSumOfNumbersArray;
            }
            System.out.println("Sum of all number arrays fetched from JSON response is " + resultSum);
        } catch (JSONException e) {
            System.out.println("Exception processing JSON" + e);
        }
    }

    public static void main(String args[]) throws JSONException {

        JSONProcessingUtils jsonProcessingUtils = new JSONProcessingUtils();
        jsonProcessingUtils.processJSONAndPrintResult(jsonProcessingUtils.getResponse());

    }
}
