package com.stud10.codelocker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
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

//            if (loginAttempts == 0) {
//                log.setEnabled(false);
//                Toast.makeText(MainActivity.this, "You have made 5 attempts, please wait 3 minutes to log again", Toast.LENGTH_SHORT).show();
//                return;
//            }
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
}
