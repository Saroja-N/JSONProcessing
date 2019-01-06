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

/**
 * The JSONProcessingUtils program implements an application that
 * processes a given JSON array with attributes - key, numbers
 * where numbers is expected to be an array of integers and prints
 * the sum of all individual arrays to the console
 *
 * @author  Saroja Naidu
 */
public class JSONProcessingUtils {

    private static final String PROPS_FILE_NAME = "application.properties";
    private static final String JSON_ATTR_NAME = "numbers";

    /**
     * This method fetches url to be consumed, from application properties
     * @return String The String containing the http url to be consumed,
     * Returns an empty string when url not available
     */
    private String getUrl() {
        String url = "";
        Properties properties = new Properties();
        try (InputStream inputStream = JSONProcessingUtils.class.getClassLoader().getResourceAsStream(PROPS_FILE_NAME)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
            url = properties.getProperty("http.url");
        } catch (IOException e) {
            System.out.println("Problem fetching url from application properties, exception occurred is - " + e);
            return "";
        }
        return url;
    }

    /**
     * This method invokes a GET request on a http url and returns the
     * JSON response as a JSONArray
     * @return JSONArray This is the output of the HTTP GET call,
     * as a JSONArray and an empty JSONArray if any exception is
     * encountered while reading the output JSON
     */
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
            System.out.println("Invalid URL, exception encountered is :"+e);
            return responseJSONArray;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            responseJSONArray = new JSONArray(response.toString());
        } catch (UnsupportedOperationException | IOException | JSONException e) {
            System.out.println("Problem fetching JSON response, detailed exception is : "+e);
        }
        return responseJSONArray;
    }

    /**
     * This method processes a JSONArray and prints sum of all arrays
     * with key - numbers
     * @param jsonArray JSONArray on which sum of all inner arrays
     * @return Output of the HTTP GET call, as a JSONArray
     * and an empty JSONArray if any exception is encountered
     * while reading the output JSON
     */
    private void processJSONAndPrintResult(JSONArray jsonArray) {

        if(jsonArray == null | jsonArray.isEmpty()){
            System.out.println("Given JSONArray is either null or empty, cannot proceed further for calculation. Exiting..");
            return;
        }
        int totalSum = 0;
        JSONObject tempJSONObject;
        JSONArray tempJSONArray = new JSONArray();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                tempJSONObject = jsonArray.getJSONObject(i);
                if(tempJSONObject != null){
                    tempJSONArray = tempJSONObject.getJSONArray(JSON_ATTR_NAME);
                }
                int currentArraySum = 0;
                for (int j = 0; j < tempJSONArray.length(); j++) {
                    currentArraySum += tempJSONArray.getInt(j);
                }
                System.out.println("Sum of current array :" + currentArraySum);
                totalSum += currentArraySum;
            }
            System.out.println("Sum of all number arrays fetched from JSON response is " + totalSum);
        } catch (JSONException e) {
            System.out.println("Exception processing JSON" + e);
        }
    }

    public static void main(String args[]) {

        JSONProcessingUtils jsonProcessingUtils = new JSONProcessingUtils();
        jsonProcessingUtils.processJSONAndPrintResult(jsonProcessingUtils.getResponse());

    }
}
