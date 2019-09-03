package com.stud10.codelocker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText username, password;
    private int loginAttempts = 5;
    private int reloginWaitTime = 180000; //ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page);
    }

    public void login(View view) throws InterruptedException {
        this.username = (EditText) findViewById(R.id.username);
        this.password = (EditText) findViewById(R.id.password);
        final Button log = (Button) findViewById(R.id.login);

        boolean userExists = DatabaseManager.findUsername(username.getText().toString());
        boolean correctPass = DatabaseManager.getPassword(username.getText().toString(), password.getText().toString());

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
}
