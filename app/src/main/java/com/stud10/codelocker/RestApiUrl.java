package com.stud10.codelocker;

enum RestApiUrl {

    GETUSER("http://192.168.0.19:3002/get_users"),
    CREATEUSER ("http://192.168.0.19:3002/create_users"),
    USERCOUNT ("http://192.168.0.19:3002/user_count"),
    GETPASSWORD ("http://192.168.0.19:3002/password"),
    USERNAMEOCCURRENCE ("http://192.168.0.19:3002/username_occurrence"),
    EMAILOCCURRENCE ("http://192.168.0.19:3002/email_occurrence");

    private String endpoint;

    RestApiUrl(String endpoint) {
        this.endpoint = endpoint;
    }

    public String endpoint(){
        return endpoint;
    }

    public String endpoint(String path1){
        return endpoint + "/" + path1;
    }

    public String endpoint(String path1, String path2){
        return endpoint + "/" + path1 + "/" + path2;
    }
}
