package com.stud10.codelocker;

import java.util.ArrayList;

public class bleh_AppContentList {

    public static ArrayList<Platform> createAppList() {
        ArrayList<Platform> appContentList = new ArrayList<Platform>();

        //run endpoint to get platform count
        int dummyPlatformCount = 7;

        //run endpoint to get credentials for each
        String dummyAppName = "dummyAppName";
        String dummyUsername = "dummyUsername";
        String dummyPassword = "dummyPassword";

        for(int i = 0; i < dummyPlatformCount; i++) {
            Platform platformItem = new Platform(dummyAppName, dummyUsername, dummyPassword);
            appContentList.add(platformItem);
        }

        return appContentList;
    }
}

//in the platforms db, sort all the apps for a user by it's UUID and make an arraylist out of that
//another table for platform count
