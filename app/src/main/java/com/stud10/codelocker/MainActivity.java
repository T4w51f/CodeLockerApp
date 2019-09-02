package com.stud10.codelocker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page);
    }

    private int counter = 5;

    public void login(View view) {
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);

        Button log = (Button) findViewById(R.id.login);
        boolean correctPass = DatabaseManager.getPassword(username.getText().toString(), password.getText().toString());
        if(correctPass){
            setContentView(R.layout.activity_main);
        } else{
            if(counter == 0){
                 log.setEnabled(false);
            }
            counter--;
        }


    }
}
