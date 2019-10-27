package com.stud10.codelocker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = HttpHandler.class.getSimpleName();;
    EditText username, password, email;
    private int loginAttempts = 5;
    private int reloginWaitTime = 180000; //ms

    //endpoints
    private static String baseUrl = "http://192.168.0.19:3002";
    private static String getUser = "/get_users";
    private static String createUser = "/create_users";
    private static String userCount = "/user_count";
    private static String getPassword = "/password";
    private static String usernameOccurrence = "/username_occurrence";
    private static String emailOccurrence = "/email_occurrence";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page);
    }

    public void login(View view) {
        this.username = (EditText) findViewById(R.id.username);
        this.password = (EditText) findViewById(R.id.password);
        final Button log = (Button) findViewById(R.id.login);

        if(username.getText().toString().equals("") || password.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Please fill in both fields", Toast.LENGTH_LONG).show();
            return;
        }

        //temporary fix
        overrideNetworkThreadPolicy();

        boolean userExists = userExists(username.getText().toString());
        boolean correctPass = checkPassword(username.getText().toString(), password.getText().toString());

        if(userExists && correctPass){
            loginAttempts = 5;
            setContentView(R.layout.activity_main);
        } else if(!userExists){
            incorrectUsernameError();
        } else if(!correctPass) {
            incorrectPasswordError();
            username.setError(null);
            loginAttempts--;

            if(loginAttempts == 0) {
                log.setEnabled(false);
                Toast.makeText(MainActivity.this, "You have made 5 attempts, please wait 3 minutes to log again", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginAttempts = 5;
                        log.setEnabled(true);
                    }
                }, reloginWaitTime);
            }
        }
    }

    private void incorrectPasswordError(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        password.startAnimation(shake);
        password.setError("Password is incorrect");
    }

    private void incorrectUsernameError(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        username.startAnimation(shake);
        username.setError("This Username does not exist");
    }

    private void incorrectEmailError(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        email.startAnimation(shake);
        email.setError("The email entered is invalid");
    }

    public void goToRegister(View view) {
        setContentView(R.layout.registration_page);
    }

    public void returnToLogin(View view) {
        setContentView(R.layout.login_page);
    }

    public void register(View view) {
        EditText reg_username = (EditText) findViewById(R.id.username);
        EditText reg_password = (EditText) findViewById(R.id.password);
        EditText firstname = (EditText) findViewById(R.id.firstname);
        EditText lastname = (EditText) findViewById(R.id.lastname);
        this.email = (EditText) findViewById(R.id.email);

        if(
                reg_username.getText().toString().equals("") ||
                reg_password.getText().toString().equals("") ||
                firstname.getText().toString().equals("") ||
                lastname.getText().toString().equals("") ||
                email.getText().toString().equals("")
        ) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        //check username availability
        boolean usernameAvailable = !userExists(reg_username.getText().toString());

        //Email format check
        boolean correctEmailFormat = emailFormatCheck(email.getText().toString());
        if(!correctEmailFormat) {
            incorrectEmailError();
            return;
        }

        //Temporary fix
        overrideNetworkThreadPolicy();

        //Flags to verify if existing users have the same username or email ID
        boolean emailAvailable = !emailExists(email.getText().toString());

        if(emailAvailable && usernameAvailable){
            boolean userAccountCreatedFlag = createUser(firstname.getText().toString(),
                    lastname.getText().toString(),
                    reg_username.getText().toString(),
                    reg_password.getText().toString(),
                    email.getText().toString());


            if(userAccountCreatedFlag){
                Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
            }

        } else if(!usernameAvailable){
            Toast.makeText(MainActivity.this, "Username not available", Toast.LENGTH_LONG).show();
        } else if(!emailAvailable){
            Toast.makeText(MainActivity.this, "Email not available", Toast.LENGTH_LONG).show();
        }
    }

    private boolean emailFormatCheck(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(!email.matches(regex)){
            Log.e(TAG, "Email incorrectly formatted!");
            return false;
        }
        return true;
    }

    public int getUsernameCount(String endpointUsername){
        // Making a request to url and getting response
        String url = baseUrl + usernameOccurrence + "/" + endpointUsername;
        String jsonStr = httpResponseString(url, "GET", null);

        HashMap<String, String> jsonResponseKeys = new HashMap<>();
        jsonResponseKeys.put("count", null);

        return Integer.valueOf(jsonResponseMap(jsonResponseKeys, jsonStr).get("count"));
    }

    public int getEmailCount(String endpointEmail){
        // Making a request to url and getting response
        String url = baseUrl + emailOccurrence + "/" + endpointEmail;
        String jsonStr = httpResponseString(url, "GET", null);

        HashMap<String, String> jsonResponseKeys = new HashMap<>();
        jsonResponseKeys.put("count", null);

        return Integer.valueOf(jsonResponseMap(jsonResponseKeys, jsonStr).get("count"));
    }

    public int getPassword(String username, String password){
        // Making a request to url and getting response
        String url = baseUrl + getPassword + "/" + username + "/" + password;
        String jsonStr = httpResponseString(url, "GET", null);

        HashMap<String, String> jsonResponseKeys = new HashMap<>();
        jsonResponseKeys.put("count", null);

        return Integer.valueOf(jsonResponseMap(jsonResponseKeys, jsonStr).get("count"));
    }

    public boolean userExists(String username){
        int count = getUsernameCount(username);
        return (count > 0);
    }

    public boolean emailExists(String email){
        int count = getEmailCount(email);
        return (count > 0);
    }

    public boolean checkPassword(String username, String password){
        int count = getPassword(username, password);
        return (count > 0);
    }

    public int getNextUserIdKey(){
        // Making a request to url and getting response
        String url = baseUrl + userCount;
        String jsonStr = httpResponseString(url, "GET", null);

        HashMap<String, String> jsonResponseKeys = new HashMap<>();
        jsonResponseKeys.put("count", null);

        return Integer.valueOf(jsonResponseMap(jsonResponseKeys, jsonStr).get("count")) + 1;
    }

    public boolean createUser(String firstname, String lastname, String reg_username, String reg_password, String email){
        Timestamp created_at = getTimestamp();
        Timestamp updated_at = getTimestamp();
        UUID userID;

        //count user, add one, make that id

        //Generating UUID
        byte[] bytes;
        try {
            bytes = reg_username.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        UUID uuid = UUID.nameUUIDFromBytes(bytes);
        userID = uuid;

        //endpoint
        // Making a request to url and getting response
        String url = baseUrl + createUser;

        String id = String.valueOf(getNextUserIdKey());
        //requestBody
        JsonObject gson = new JsonObject();
        gson.addProperty("id", id);
        gson.addProperty("name", firstname + " " + lastname);
        gson.addProperty("email", email);
        gson.addProperty("password", reg_password);
        gson.addProperty("created_at", String.valueOf(created_at));
        gson.addProperty("updated_at", String.valueOf(updated_at));
        gson.addProperty("username", reg_username);
        gson.addProperty("user_id", String.valueOf(userID));

        String requestBody = gson.toString();
        String jsonStr = httpResponseString(url, "POST", requestBody);

        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            Log.e(TAG, jsonStr);
            return true;

        } else {
            Log.e(TAG, "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG).show();
                }
            });
            return false;
        }
    }

    public String httpResponseString(String url, String httpMethodType, String requestBody){
        HttpHandler sh = new HttpHandler();
        String jsonStr;
        if(httpMethodType.equals("GET")) jsonStr = sh.makeGetServiceCall(url);
        else if(httpMethodType.equals("POST")) jsonStr = sh.makePostServiceCall(url, requestBody);
        else jsonStr = "INCORRECT HTTP METHOD TYPE";

        return jsonStr;
    }

    /***
     * Retrieves the current timestamp
     * @return the current timestamp in local time
     */
    private static java.sql.Timestamp getTimestamp(){
        Date date = new Date();
        long time = date.getTime();
        java.sql.Timestamp ts = new java.sql.Timestamp(time);
        return ts;
    }

    private void overrideNetworkThreadPolicy() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private HashMap<String, String> jsonResponseMap (HashMap<String, String> jsonKeysMap, String jsonStr){
        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                for(String keys : jsonKeysMap.keySet()){
                    jsonKeysMap.put(keys, jsonObj.getString(keys));
                }

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        return jsonKeysMap;
    }

    //TODO clean up code
    //TODO verify email address
    //TODO confirmation email
    //TODO error handling
    //TODO rerun server upon failure
    //TODO change password for existing user
    //TODO create more classes to split the functions
    //TODO success message upon account registration
    //TODO null handling, e.g. login crashes
    //TODO strings should be in one file
    //TODO server disconnection causes freeze
}
