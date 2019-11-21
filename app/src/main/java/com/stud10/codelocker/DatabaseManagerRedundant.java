package com.stud10.codelocker;

import android.os.StrictMode;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


public class DatabaseManagerRedundant {

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

    public static boolean findUsername(String username){
        int count = 0;
        String query = "SELECT COUNT(username) FROM app_user WHERE username = \'" + username + "\'";
        try (Connection con = DriverManager.getConnection(url, user, db_password);
             PreparedStatement pst = con.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                count = rs.getInt(1);
            }
            if (count == 1){
                return true;
            }

        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return false;
    }

    public static boolean createUser(String firstName, String lastName, String username, String password, String email){
        Timestamp created_at = getTimestamp();
        Timestamp updated_at = getTimestamp();
        UUID userID = null;

        //Generating UUID
        byte[] bytes = new byte[0];
        bytes = username.getBytes(StandardCharsets.UTF_8);
        UUID uuid = UUID.nameUUIDFromBytes(bytes);
        userID = uuid;

        String query = "INSERT INTO app_user(user_id, name, email, password, created_at, updated_at, username) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, user, db_password);
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setObject(1, userID);
            pst.setString(2, firstName + " " + lastName);
            pst.setString(3, email);
            pst.setString(4, password);
            pst.setTimestamp(5, created_at);
            pst.setTimestamp(6, updated_at);
            pst.setString(7, username);
            pst.executeUpdate();
            con.close();

        } catch (SQLException ex) {
            ex.getStackTrace();
            return false;
        }
        return true;
    }

    /***
     * Retrieves the current timestamp
     * @return the current timestamp in local time
     */
    private static Timestamp getTimestamp(){
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
    public static boolean validateUsername(String username){
        double count = 0;
        String query = "SELECT COUNT(username) FROM app_user WHERE username = \'" + username + "\'";
        try (Connection con = DriverManager.getConnection(url, user, db_password);
             PreparedStatement pst = con.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                count = rs.getInt(1);
            }
            if (count > 0){
                System.out.println("This username is not available!");
                return false;
            }

        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return true;
    }

    /***
     * Checks the database whether a user account is already associated with the same email ID
     * @param email unique user email ID
     * @return true if the email is available for user
     */
    public static boolean validateEmail(String email){
        double count = 0;

        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(!email.matches(regex)){
            System.out.println("Email incorrectly formatted!");
            return false;
        }

        String query = "SELECT COUNT(email) FROM app_user WHERE email = \'" + email + "\'";
        try (Connection con = DriverManager.getConnection(url, user, db_password);
             PreparedStatement pst = con.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                count = rs.getInt(1);
            }
            if (count > 0){
                System.out.println("This email address is associated with another account, try a different address");
                return false;
            }

        } catch (SQLException ex) {
            ex.getStackTrace();
        }

        return true;
    }
}
