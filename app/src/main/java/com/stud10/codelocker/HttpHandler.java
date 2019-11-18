package com.stud10.codelocker;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

    /***
     * Constructor for this class
     */
    public HttpHandler() {
    }

    /***
     * Runs a GET request-response REST call
     * @param reqUrl Endpoint url to call
     * @return Response string from the call
     */
    public String makeGetServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    /***
     * Runs a POST request-response REST call
     * @param reqUrl Endpoint url to call
     * @return Response string from the call
     */
    public String makePostServiceCall(String reqUrl, String jsonInputString){
        try {
            String charset = "UTF-8";
            URL url = new URL(reqUrl);
            URLConnection connection = url.openConnection();

            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(jsonInputString.getBytes(charset));
            } catch (IOException e) {
                e.printStackTrace();
            }

            InputStream response = null;
            try {
                response = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    /**
     * Converts an inputstream to an easy to read string
     * @param is
     * @return the converted string
     */
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
