package com.stud10.codelocker;

public class Platform {
    private String appname;
    private bleh_Credentials blehCredentials;

    public Platform(String appname, String username, String password) {
        this.appname = appname;
        this.blehCredentials = new bleh_Credentials(username, password);
    }

    public String getAppName() {
        return appname;
    }

    public bleh_Credentials getBlehCredentials() {
        return blehCredentials;
    }
}

//in the platforms db, sort all the apps for a user by it's UUID and make an arraylist out of that
//another table for platform count
