package com.stud10.codelocker;

import java.util.HashMap;

public class bleh_Credentials {
    private String username;
    private String password;

    public bleh_Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public HashMap<String, String> getCredentials () {
        HashMap<String, String> credentialMap = new HashMap<>();
        credentialMap.put(username, password);
        return credentialMap;
    }
}

//in the platforms db, sort all the apps for a user by it's UUID and make an arraylist out of that
