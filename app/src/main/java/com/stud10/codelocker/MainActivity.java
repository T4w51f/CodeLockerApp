package com.stud10.codelocker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements CredentialModal.OnInputListener{
    private static final String TAG = HttpHandler.class.getSimpleName();
    EditText username, password, email;
    private String user_id;
    private int loginAttempts = 5;
    private int reloginWaitTime = 180000; //ms

    //For the recyclerView
    private ArrayList<String> appList = new ArrayList<>();
    private ArrayList<String> usernameList = new ArrayList<>();
    private ArrayList<String> pwdList = new ArrayList<>();
    private RecyclerViewAdapter adapter;

    //For the 'Add new credentials' modal
    private FloatingActionButton floatingPlus;
    public String newCredentialAppname, newCredentialUsername, newCredentialPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page);
    }

    private void popCredentialsModal(){
        floatingPlus = findViewById(R.id.fab);
        floatingPlus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: opening dialog");
                CredentialModal modal = new CredentialModal();
                modal.show(getSupportFragmentManager(), "CredentialModal");
            }
        });

    }

    public boolean createCredential(){
        Timestamp created_at = getTimestamp();
        Timestamp updated_at = getTimestamp();

        //count user, add one, make that id

        //endpoint
        // Making a request to url and getting response
        overrideNetworkThreadPolicy();
        String url = RestApiUrl.ADDCREDENTIALS.endpoint();

        String id = null;
        try {
            id = getNextCredentialIdKey();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //requestBody
        JsonObject gson = new JsonObject();
        gson.addProperty("id", id);
        gson.addProperty("user_id", this.user_id);
        gson.addProperty("app_name", this.newCredentialAppname);
        gson.addProperty("username", this.newCredentialUsername);
        gson.addProperty("password", this.newCredentialPassword);
        gson.addProperty("created_at", String.valueOf(created_at));
        gson.addProperty("updated_at", String.valueOf(updated_at));

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

    private String getNextCredentialIdKey() throws JSONException {
        // Making a request to url and getting response
        overrideNetworkThreadPolicy();
        String url = RestApiUrl.CREDENTIALSCOUNT.endpoint(user_id);
        String jsonStr = httpResponseString(url, "GET", null);
        JSONObject jsonObj =  new JSONObject(jsonStr);

        return String.valueOf(Integer.valueOf(jsonObj.get("count").toString()) + 1);
    }

    private void runContentPage(){
        setContentView(R.layout.recycler_layout);
        Log.d("RecyclerView", "onCreate: started.");
        try {
            initRVLists();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        popCredentialsModal();
    }

    private void initRVLists() throws JSONException {
        Log.d("RecyclerView", "onCreate: init RVLists.");

        overrideNetworkThreadPolicy();
        String url = RestApiUrl.CREDENTIALS.endpoint(user_id);
        String jsonResponse = httpResponseString(url, "GET", null);

        if(jsonResponse != "[]"){
            JSONArray jsonCredentialsMap = new JSONArray(jsonResponse);

            for(int i = 0; i < jsonCredentialsMap.length(); i++){
                JSONObject credentials = jsonCredentialsMap.getJSONObject(i);
                appList.add(credentials.get("app_name").toString());
                usernameList.add(credentials.get("username").toString());
                pwdList.add(credentials.get("password").toString());
            }
        }

        initRecyclerView();
    }

    private void updateRVLists() throws JSONException {
        Log.d("RecyclerView", "onAdd: updating RVLists.");

        overrideNetworkThreadPolicy();
        String url = RestApiUrl.CREDENTIALS.endpoint(user_id);
        String jsonResponse = httpResponseString(url, "GET", null);

        appList.clear();
        usernameList.clear();
        pwdList.clear();

        if(jsonResponse != "[]"){
            JSONArray jsonCredentialsMap = new JSONArray(jsonResponse);

            for(int i = 0; i < jsonCredentialsMap.length(); i++){
                JSONObject credentials = jsonCredentialsMap.getJSONObject(i);
                appList.add(credentials.get("app_name").toString());
                usernameList.add(credentials.get("username").toString());
                pwdList.add(credentials.get("password").toString());
            }
        }

        refreshContentPage();
    }



    private void initRecyclerView(){
        Log.d("RecyclerView", "onCreate: init RecyclerView.");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        this.adapter = new RecyclerViewAdapter(appList, usernameList, pwdList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void refreshContentPage() {
        this.adapter.notifyDataSetChanged();
    }

    /***
     * Task executed upon selecting the Login button
     * @param view
     */
    public void login(View view) {
        this.username = findViewById(R.id.username);
        this.password = findViewById(R.id.password);
        final Button log = findViewById(R.id.login);

        if(username.getText().toString().equals("") || password.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Please fill in both fields", Toast.LENGTH_LONG).show();
            return;
        }

        //temporary fix
        overrideNetworkThreadPolicy();

        boolean userExists = userExists(username.getText().toString());

        boolean correctPass = false;
        try {
            correctPass = checkPassword(username.getText().toString(), password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if(userExists && correctPass){
            loginAttempts = 5;

            try {
                this.user_id = getUserUUID(username.getText().toString(), password.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }

            runContentPage();
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

    private String getUserUUID(String username, String password) throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException {
        overrideNetworkThreadPolicy();
        PasswordHashPBKDF2 pwdHashFunction = new PasswordHashPBKDF2();
        String salt = getSalt(username);
        String hashedPassword = pwdHashFunction.checkPassHash(password, salt);

        String url = RestApiUrl.USERID.endpoint(username, hashedPassword);
        String jsonStr = httpResponseString(url, "GET", null);
        JSONObject jsonObject =  new JSONObject(jsonStr);
        return String.valueOf(jsonObject.get("user_id"));
    }

    /***
     * Directs user to the account registration page
     * @param view
     */
    public void goToRegister(View view) {
        setContentView(R.layout.registration_page);
    }

    /***
     * Directs user to the login page
     * @param view
     */
    public void returnToLogin(View view) {
        setContentView(R.layout.login_page);
    }

    /***
     * Directs user to the login page
     * @param view
     */
    public void logout(View view) {
        appList.clear();
        usernameList.clear();
        pwdList.clear();
        refreshContentPage();
        setContentView(R.layout.login_page);
    }


    /***
     * Task executed upon selecting the registration button
     * @param view
     */
    public void register(View view) {
        PasswordHashPBKDF2 pwdHashFunction = null;
        String hashedPassword = null;

        try {
            pwdHashFunction = new PasswordHashPBKDF2();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        EditText reg_username = findViewById(R.id.username);
        EditText reg_password = findViewById(R.id.password);
        EditText firstname = findViewById(R.id.firstname);
        EditText lastname = findViewById(R.id.lastname);
        this.email = findViewById(R.id.email);

        try {
            hashedPassword = pwdHashFunction.hashPBKDF2(reg_password.getText().toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        //Temporary fix
        overrideNetworkThreadPolicy();

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


        //Flags to verify if existing users have the same username or email ID
        boolean emailAvailable = !emailExists(email.getText().toString());

        if(emailAvailable && usernameAvailable){
            boolean userAccountCreatedFlag = createUser(firstname.getText().toString(),
                    lastname.getText().toString(),
                    reg_username.getText().toString(),
                    hashedPassword,
                    email.getText().toString(),
                    pwdHashFunction.storeSalt());


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

    /***
     * Validates the email formatting but not its existence
     * @param email
     * @return true if email is formatted correctly otherwise false
     */
    private boolean emailFormatCheck(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(!email.matches(regex)){
            Log.e(TAG, "Email incorrectly formatted!");
            return false;
        }
        return true;
    }

    /***
     * Deduces the number of username in the database with the same username argument
     * @param endpointUsername
     * @return The integer number of the username that matches
     */
    public int getUsernameCount(String endpointUsername){
        // Making a request to url and getting response
        String url = RestApiUrl.USERNAMEOCCURRENCE.endpoint(endpointUsername);
        String jsonStr = httpResponseString(url, "GET", null);

        HashMap<String, String> jsonResponseKeys = new HashMap<>();
        jsonResponseKeys.put("count", null);

        return Integer.valueOf(jsonResponseMap(jsonResponseKeys, jsonStr).get("count"));
    }

    /***
     * Deduces the number of email in the database with the same email argument
     * @param endpointEmail
     * @return The integer number of the email that matches
     */
    public int getEmailCount(String endpointEmail){
        // Making a request to url and getting response
        String url = RestApiUrl.EMAILOCCURRENCE.endpoint(endpointEmail);
        String jsonStr = httpResponseString(url, "GET", null);

        HashMap<String, String> jsonResponseKeys = new HashMap<>();
        jsonResponseKeys.put("count", null);

        return Integer.valueOf(jsonResponseMap(jsonResponseKeys, jsonStr).get("count"));
    }

    /***
     * Validates the password for a given user against the database
     * @param username The non-null account username
     * @param password The non-null account password
     * @return A non-zero number if the password matches the username
     */
    public int getPassword(String username, String password){
        // Making a request to url and getting response
        String url = RestApiUrl.GETPASSWORD.endpoint(username, password);
        String jsonStr = httpResponseString(url, "GET", null);

        HashMap<String, String> jsonResponseKeys = new HashMap<>();
        jsonResponseKeys.put("count", null);

        return Integer.valueOf(jsonResponseMap(jsonResponseKeys, jsonStr).get("count"));
    }

    /***
     * Checks if username already exists in the database
     * @param username
     * @return True if it exists otherwise false
     */
    public boolean userExists(String username){
        int count = getUsernameCount(username);
        return (count > 0);
    }

    /***
     * Checks in email already exists in the database
     * @param email
     * @return True if it exists otherwise false
     */
    public boolean emailExists(String email){
        int count = getEmailCount(email);
        return (count > 0);
    }

    /***
     * Checks if password entered belongs to the specific user
     * by matching against the database
     * @param username
     * @param password
     * @return True if password and username entered are correct
     */
    public boolean checkPassword(String username, String password) throws JSONException, InvalidKeySpecException, NoSuchAlgorithmException {
        overrideNetworkThreadPolicy();
        PasswordHashPBKDF2 pwdHashFunction = new PasswordHashPBKDF2();
        String salt = getSalt(username);
        String hashedPassword = pwdHashFunction.checkPassHash(password, salt);
        int count = getPassword(username, hashedPassword);
        return (count > 0);
    }

    private String getSalt(String username) throws JSONException {
        String url = RestApiUrl.SALT.endpoint(username);
        String jsonStr = httpResponseString(url, "GET", null);

        JSONObject jsonObject = new JSONObject(jsonStr);
        String salt = jsonObject.getString("salt");
        return salt;
    }

    /***
     * Gets the primary key ID for a new entry in the user database
     * @return The non-null integer primary key ID
     */
    public int getNextUserIdKey(){
        // Making a request to url and getting response
        String url = RestApiUrl.USERCOUNT.endpoint();
        String jsonStr = httpResponseString(url, "GET", null);

        HashMap<String, String> jsonResponseKeys = new HashMap<>();
        jsonResponseKeys.put("count", null);

        return Integer.valueOf(jsonResponseMap(jsonResponseKeys, jsonStr).get("count")) + 1;
    }

    /***
     * Creates a new user entry in the database
     * @param firstname
     * @param lastname
     * @param reg_username
     * @param reg_password
     * @param email
     * @return True if the user entry is successfully created, otherwise false
     */
    public boolean createUser(String firstname, String lastname, String reg_username, String reg_password, String email, String salt){
        Timestamp created_at = getTimestamp();
        Timestamp updated_at = getTimestamp();
        UUID userID;

        //count user, add one, make that id

        //Generating UUID
        byte[] bytes;
        bytes = reg_username.getBytes(StandardCharsets.UTF_8);
        UUID uuid = UUID.nameUUIDFromBytes(bytes);
        userID = uuid;

        //endpoint
        // Making a request to url and getting response
        String url = RestApiUrl.CREATEUSER.endpoint();

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
        gson.addProperty("salt", salt);

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

    /**
     * Calls the appropriate REST endpoint to obtain data from the database
     * @param url
     * @param httpMethodType
     * @param requestBody
     * @return a JSON response from the endpoint call
     */
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

    /***
     * Temporary fix to avoid Network Thread Policy Exception
     */
    private void overrideNetworkThreadPolicy() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    /***
     * Maps the JSON response into a map with desired keys
     * @param jsonKeysMap
     * @param jsonStr
     * @return A map pairing the values for the desired keys from the JSON response
     */
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

    /***
     * Animation settings when incorrect password is entered
     */
    private void incorrectPasswordError(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        password.startAnimation(shake);
        password.setError("Password is incorrect");
    }

    /***
     * Animation settings when incorrect username is entered
     */
    private void incorrectUsernameError(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        username.startAnimation(shake);
        username.setError("This Username does not exist");
    }

    /***
     * Animation settings when incorrect email is entered
     */
    private void incorrectEmailError(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        email.startAnimation(shake);
        email.setError("The email entered is invalid");
    }

    @Override
    public void sendInput(String input, int idx) {
        Log.d(TAG, "sendInput: got the input: " + input);
        if(idx == 0) this.newCredentialAppname = input;
        else if(idx == 1) this.newCredentialUsername = input;
        else if(idx == 2) this.newCredentialPassword = input;
        else if(idx == 3) createCredential();
        else {
            try {
                updateRVLists();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //TODO clean up code
    //TODO verify email address
    //TODO confirmation email
    //TODO rerun server upon failure
    //TODO change password for existing user
    //TODO create more classes to split the functions
    //TODO strings should be in one file
    //TODO server disconnection causes freeze
    //TODO handle null response from rest call
    //TODO take largest primary key and not based off count from the db
    //TODO make everything private
    //TODO Use hash function
}
