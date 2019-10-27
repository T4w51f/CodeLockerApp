package com.androidapp.codelocker;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


public class DatabaseManagerRESTApiRedundant {
    private static String baseUrl = "http://192.168.0.19:3002";
    private static String getUser = "/get_users";
    private static String createUser = "/create_users";
    private static String userCount = "/user_count";
    private static String getPassword = "/password";
    private static String usernameOccurrence = "/username_occurrence";
    private static String emailOccurrence = "/email_occurrence";

    private static String usernameCount = "";

    public static JsonObjectRequest createUser(String firstName, String lastName, String username, String password, String email) throws JSONException {
        Timestamp created_at = getTimestamp();
        Timestamp updated_at = getTimestamp();
        UUID userID = null;

        //Generating UUID
        byte[] bytes = new byte[0];
        try {
            bytes = username.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        UUID uuid = UUID.nameUUIDFromBytes(bytes);
        userID = uuid;

        //Create the JSON Request Object
        JsonObject gson = new JsonObject();
        gson.addProperty("id", "PLACEHOLDER FOR COUNT");
        gson.addProperty("name", firstName + " " + lastName);
        gson.addProperty("email", email);
        gson.addProperty("password", password);
        gson.addProperty("created_at", String.valueOf(created_at));
        gson.addProperty("updated_at", String.valueOf(updated_at));
        gson.addProperty("username", username);
        gson.addProperty("user_id", String.valueOf(userID));

        JSONObject JsonRequest = new JSONObject(gson.toString());

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                baseUrl + createUser,
                JsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("RESPONSE", response.toString());
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", error.toString());
                    }
                }
        );
        return objectRequest;
    }

    public static JsonObjectRequest getPassword(String username) throws JSONException {
        //Create the JSON Request Object
        JsonObject gson = new JsonObject();
        gson.addProperty("username", username);

        JSONObject JsonRequest = new JSONObject(gson.toString());

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                baseUrl + getPassword,
                JsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("RESPONSE", response.toString());
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", error.toString());
                    }
                }
        );
        return objectRequest;
    }

    public static JsonObjectRequest findUsername(String username) throws JSONException {
        //Create the JSON Request Object
        JsonObject gson = new JsonObject();
        gson.addProperty("username", username);

        JSONObject JsonRequest = new JSONObject(gson.toString());
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    usernameCount = response.get("count").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        };

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                baseUrl + usernameOccurrence,
                JsonRequest,
                listener,
                errorListener
        );

        return objectRequest;
    }

    public static String getUsernameCount (){
        return usernameCount;
    }


    /***
     * Retrieves the current timestamp
     * @return the current timestamp in local time
     */
    private static Timestamp getTimestamp() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        return ts;
    }

    /***
     * Checks the database whether a user of the same username already exists or not
     * @param username Username of new user adding an entry
     * @return true if the username is available for use
     */
    public static JsonObjectRequest validateUsername(String username) throws JSONException {
        //Create the JSON Request Object
        JsonObject gson = new JsonObject();
        gson.addProperty("username", username);

        JSONObject JsonRequest = new JSONObject(gson.toString());

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                baseUrl + usernameOccurrence,
                JsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("RESPONSE", response.toString());
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", error.toString());
                    }
                }
        );
        return objectRequest;
    }

    /***
     * Checks the database whether a user account is already associated with the same email ID
     * @param email unique user email ID
     * @return true if the email is available for user
     */
    public static JsonObjectRequest validateEmail(String email) throws JSONException {
        //Create the JSON Request Object
        JsonObject gson = new JsonObject();
        gson.addProperty("email", email);

        JSONObject JsonRequest = new JSONObject(gson.toString());

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                baseUrl + emailOccurrence,
                JsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("RESPONSE: ", response.toString());
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR: ", error.toString());
                    }
                }
        );
        return objectRequest;
    }
}
