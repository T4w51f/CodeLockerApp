package com.stud10.codelocker;

import android.os.StrictMode;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;


public class DatabaseManager {
    private static String email;
    //private static String username;
    private static String firstName;
    private static String lastName;
    private static UUID userID;
    //private static String password;
    private static Timestamp created_at;
    private static Timestamp updated_at;

    private static String url = "jdbc:postgresql://ec2-23-21-91-183.compute-1.amazonaws.com:5432/d6kp2l82786968";
    private static String user = "tnyjufhgxqoxgb";
    private static String db_password = "cf6f2019a804c631bdd30e05d6ffca6c7ff2565b1749882c01759476492e3ce4";

    public static boolean getPassword(String username, String password){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String query = "SELECT password FROM app_user WHERE username = '" + username + "'";
        String actualPassword = "";
        try (Connection con = DriverManager.getConnection(url, user, db_password);
             PreparedStatement pst = con.prepareStatement(query)) {

            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                actualPassword = rs.getString("password");
            }

            System.out.println(actualPassword);
            con.close();

        } catch (SQLException ex) {
            ex.getStackTrace();
        }

        return (actualPassword.equals(password));
    }
}
