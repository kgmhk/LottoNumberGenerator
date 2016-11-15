package com.gkwak.lottonumbergenerator.libs;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gihyun on 2016-08-06.
 */
public class HttpRequest {
    static String DEBUG_TAG = "HTTP_REQUEST";
    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    public JSONObject downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            Log.i(DEBUG_TAG, myurl);
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();


            BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

            char[] buffer = new char[1024];

            String jsonString = new String();

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();

            jsonString = sb.toString();

            System.out.println("JSON: " + jsonString);

            JSONObject obj = null;
            try {
                obj = new JSONObject(jsonString);
            } catch (JSONException e) {
                String exceoptionJson = "{bnusNo:0,firstWinamnt:0,totSellamnt:0,returnValue:fail,drwtNo3:1,drwtNo2:1," +
                        "drwtNo1:1,drwtNo6:1,drwtNo5:1,drwtNo4:1,drwNoDate:0000-00-00,drwNo:500,firstPrzwnerCo:0}";
                try {
                    obj = new JSONObject(exceoptionJson);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

            return obj;

//
//            int response = conn.getResponseCode();
//            Log.d(DEBUG_TAG, "The response is: " + response);
//            is = conn.getInputStream();
//
//            // Convert the InputStream into a string
//            String contentAsString = readIt(is, len);
//            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }


}
