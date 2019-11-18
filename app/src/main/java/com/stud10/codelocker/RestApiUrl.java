package com.stud10.codelocker;

enum RestApiUrl {

    //app_user table
    GETUSER("http://192.168.0.19:3002/get_users"),
    CREATEUSER ("http://192.168.0.19:3002/create_users"),
    USERCOUNT ("http://192.168.0.19:3002/user_count"),
    GETPASSWORD ("http://192.168.0.19:3002/password"),
    USERNAMEOCCURRENCE ("http://192.168.0.19:3002/username_occurrence"),
    EMAILOCCURRENCE ("http://192.168.0.19:3002/email_occurrence"),
    USERID ("http://192.168.0.19:3002/uuid"),
    ADDCREDENTIALS("http://192.168.0.19:3002/add_credentials"),
    CREDENTIALSCOUNT ("http://192.168.0.19:3002/create_users"),
    CREDENTIALS ("http://192.168.0.19:3002/credentials");

    private String endpoint;

    /***
     * Constructor for this enum class
     * @param endpoint
     */
    RestApiUrl(String endpoint) {
        this.endpoint = endpoint;
    }

    /***
     * @return the endpoint with no arguments
     */
    public String endpoint(){
        return endpoint;
    }

    /***
     * @return the endpoint with one arguments
     */
    public String endpoint(String path1){
        return endpoint + "/" + path1;
    }

    /***
     * @return the endpoint with two arguments
     */
    public String endpoint(String path1, String path2){
        return endpoint + "/" + path1 + "/" + path2;
    }
}
