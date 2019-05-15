package com.example.atividade_maps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JSONLoader {

    private String responseJSON;
    public void downloadJSON() {
        URL url;
        StringBuffer response = new StringBuffer();
        try {
            url = new URL("http://www.mocky.io/v2/5cdb4544300000640068cc7b");
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url");
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

            //Here is your json in string format
            responseJSON = response.toString();
        }
    }

    public JSONArray loadJSONobject() {

        try {
            JSONObject jsonRootObject = new JSONObject(responseJSON);

            JSONArray jsonArray = jsonRootObject.optJSONArray("pessoas");

            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
