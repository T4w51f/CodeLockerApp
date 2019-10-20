package com.stud10.codelocker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = HttpHandler.class.getSimpleName();;
    EditText username, password;
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

    public void login(View view) throws InterruptedException, JSONException {
        this.username = (EditText) findViewById(R.id.username);
        this.password = (EditText) findViewById(R.id.password);
        final Button log = (Button) findViewById(R.id.login);

        //temporary fix
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //int usernameOccurrence = getUsernameCount(username.getText().toString());

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

    public void goToRegister(View view) {
        setContentView(R.layout.registration_page);
    }

    public void returnToLogin(View view) {
        setContentView(R.layout.login_page);
    }

    public void register(View view) {
        EditText reg_username = (EditText) findViewById(R.id.username);
        EditText reg_password = (EditText) findViewById(R.id.password);
        EditText firstName = (EditText) findViewById(R.id.firstname);
        EditText lastname = (EditText) findViewById(R.id.lastname);
        EditText email = (EditText) findViewById(R.id.email);

        //Flags to verify if existing users have the same username or email ID
        boolean validUsernameFlag = DatabaseManager.validateUsername(reg_username.getText().toString());
        boolean validEmailFlag = DatabaseManager.validateEmail(email.getText().toString());

        if(validEmailFlag && validUsernameFlag){
            boolean userAccountCreatedFlag = DatabaseManager.createUser(firstName.getText().toString(),
                    lastname.getText().toString(),
                    reg_username.getText().toString(),
                    reg_password.getText().toString(),
                    email.getText().toString());

            if(!userAccountCreatedFlag){
                //Failed to create account message
            }
        } else if(!validUsernameFlag){
            //Error for username not available
        } else if(!validEmailFlag){
            //Error for email incorrect format
        }
    }

    public int getUsernameCount(String endpointUsername){
        String count = null;
        // Making a request to url and getting response
        String url = baseUrl + usernameOccurrence + "/" + endpointUsername;
        String jsonStr = httpResponseString(url, "GET");

        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                count = jsonObj.getString("count");

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

        return Integer.valueOf(count);
    }

    public int getPassword(String username, String password){
        String count = null;
        // Making a request to url and getting response
        String url = baseUrl + getPassword + "/" + username + "/" + password;
        String jsonStr = httpResponseString(url, "GET");

        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                count = jsonObj.getString("count");

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

        return Integer.valueOf(count);
    }

    public boolean userExists(String username){
        int count = getUsernameCount(username);
        return (count > 0);
    }

    public boolean checkPassword(String username, String password){
        int count = getPassword(username, password);
        return (count > 0);
    }

    public String httpResponseString(String url, String httpMethodType){
        HttpHandler sh = new HttpHandler();
        //String jsonInputString = "{\"username\": \"" + un + "\"}";
        String jsonStr = null;
        if(httpMethodType.equals("GET")) jsonStr = sh.makeGetServiceCall(url);
        else if(httpMethodType.equals("POST")) jsonStr = sh.makeGetServiceCall(url);
        else jsonStr = "INCORRECT HTTP METHOD TYPE";

        return jsonStr;
    }
}
