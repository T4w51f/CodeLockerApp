package com.stud10.codelocker;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class CodeLock {
    private static String email;
    private static String username;
    private static String firstName;
    private static String lastName;
    private static UUID userID;
    private static String password;
    private static Timestamp created_at;
    private static Timestamp updated_at;

    private static String url = "jdbc:postgresql://ec2-23-21-91-183.compute-1.amazonaws.com:5432/d6kp2l82786968";
    private static String user = "tnyjufhgxqoxgb";
    private static String db_password = "cf6f2019a804c631bdd30e05d6ffca6c7ff2565b1749882c01759476492e3ce4";

    private static Map<String, Platform> appMap = new HashMap<>();

    /***
     * Constructor to create an instance of the CodeLock app user
     * @param firstName non-empty first name string
     * @param lastName non-empty last name string
     * @param username unique user name for app
     * @param email user's email ID
     * @param password user's password to access the app
     */
    public CodeLock(String firstName, String lastName, String username, String email, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this. username = username;
        this.email = email;
        this.password = password;
        this.created_at = getTimestamp();
        this.updated_at = getTimestamp();

        //Flags to verify if existing users have the same username or email ID
        boolean validUsernameFlag = validateUsername(username);
        boolean validEmailFlag = validateEmail(email);

        if(validUsernameFlag && validEmailFlag) {
            byte[] bytes = new byte[0];
            try {
                bytes = username.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            UUID uuid = UUID.nameUUIDFromBytes(bytes);
            this.userID = uuid;
            createUser();
        }
    }

    /***
     * Checks the database whether a user of the same username already exists or not
     * @param username Username of new user adding an entry
     * @return true if the username is available for use
     */
    private boolean validateUsername(String username){
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
    private boolean validateEmail(String email){
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

    /***
     * Uses the private user credentials to create an account entry in the database server
     */
    private void createUser(){
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
        }
    }

    /***
     * Retrieves the current timestamp
     * @return the current timestamp in local time
     */
    private Timestamp getTimestamp(){
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        return ts;
    }

    /**
     * @Preconditions Email has to be validated first before it enters this method
     * @param email
     */
    private void changeEmail(String new_email){
        Scanner input = new Scanner(System.in);
        String enteredPassword = input.nextLine();
        if(enteredPassword.equals(password)){
            String query = "UPDATE app_user SET email = REPLACE(email, ?, ?) WHERE user_id = ?; UPDATE app_user SET updated_at = REPLACE(updated_at, ?, ?) WHERE user_id = ?;";
            try (Connection con = DriverManager.getConnection(url, user, db_password);
                 PreparedStatement pst = con.prepareStatement(query)) {

                pst.setString(1, email);
                pst.setString(2, new_email);
                pst.setObject(3, userID);
                pst.setTimestamp(4, updated_at);
                pst.setTimestamp(5, getTimestamp());
                pst.setObject(6, userID);
                pst.executeUpdate();
                con.close();

            } catch (SQLException ex) {
                ex.getStackTrace();
            }
            //Query to modify email based off userID
            //Update timestamp too
        } else {
            System.out.println("Failed to verify user. Try again!");
        }
    }

    public String getUsername(){
        return username;
    }

    public UUID getUUID(){
        return userID;
    }

    public void addPlatform(String app, String appUsername, String appPassword){
        Platform appPlatform = new Platform(app, appUsername, appPassword, userID);
        if(appPlatform.addToDB()){
            appMap.put(app, appPlatform);
            System.out.println("Successfully added " + app + " to storage!");
        }

    }

    public void changePlatformPassword(String app, String oldPass, String newPass){
        if(appMap.get(app).modifyPassword(oldPass, newPass)){
            System.out.println("Successfully changed password for " + app + "!");
        }
    }

    public void deletePlatform(String app){
        if(appMap.get(app).removeFromDB()){
            System.out.println("Successfully removed " + app + "!");
        }
    }

    private class Platform{
        private UUID userUID;
        private String platformName;
        private String userName;
        private String password;
        private Timestamp p_created_at;
        private Timestamp p_updated_at;

        private String url = "jdbc:postgresql://ec2-23-21-91-183.compute-1.amazonaws.com:5432/d6kp2l82786968";
        private String user = "tnyjufhgxqoxgb";
        private String db_password = "cf6f2019a804c631bdd30e05d6ffca6c7ff2565b1749882c01759476492e3ce4";

        private Platform(String platformName, String userName, String password, UUID userID){
            this.platformName = platformName;
            this.userName = userName;
            this.password = password;
            this.userUID = userID;
            this.p_created_at = getTimestamp();
            this.p_updated_at = getTimestamp();

        }

        private boolean addToDB(){
            String query = "INSERT INTO platforms(app_name, user_id, username, password, created_at, updated_at) VALUES(?, ?, ?, ?, ?, ?)";
            try (Connection con = DriverManager.getConnection(url, user, db_password);
                 PreparedStatement pst = con.prepareStatement(query)) {

                pst.setString(1, platformName);
                pst.setObject(2, userUID);
                pst.setString(3, userName);
                pst.setString(4, password);
                pst.setTimestamp(5, created_at);
                pst.setTimestamp(6, updated_at);
                pst.executeUpdate();
                con.close();

            } catch (SQLException ex) {
                ex.getStackTrace();
                return false;
            }
            return true;
        }

        private boolean modifyPassword(String oldPassword, String newPassword){
            if(!oldPassword.equals(password)){
                return false;
            }

            String query = "UPDATE platforms SET password = REPLACE(password, ?, ?) WHERE app_name = ?; UPDATE platforms SET updated_at = REPLACE(updated_at, ?, ?) WHERE app_name = ?;";
            try (Connection con = DriverManager.getConnection(url, user, db_password);
                 PreparedStatement pst = con.prepareStatement(query)) {

                pst.setString(1, oldPassword);
                pst.setString(2, newPassword);
                pst.setString(3, platformName);
                pst.setTimestamp(4, p_updated_at);
                pst.setTimestamp(5, getTimestamp());
                pst.setObject(6, platformName);
                pst.executeUpdate();
                con.close();

            } catch (SQLException ex) {
                ex.getStackTrace();
                return false;
            }
            return true;
        }

        private boolean removeFromDB(){
            String query = "DELETE FROM platforms WHERE app_name = ?;";

            try (Connection con = DriverManager.getConnection(url, user, db_password);
                 PreparedStatement pst = con.prepareStatement(query)) {

                pst.setString(1, platformName);
                pst.executeUpdate();
                con.close();

            } catch (SQLException ex) {
                ex.getStackTrace();
                return false;
            }
            return true;
        }

        private String getName(){
            return "";
        }

        private UUID getUUID(){
            return userUID;
        }
    }

}
