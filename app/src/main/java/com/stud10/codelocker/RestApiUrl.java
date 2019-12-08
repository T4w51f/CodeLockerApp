package com.stud10.codelocker;

enum RestApiUrl {

    //app_user table
    GETUSER("https://codelocker-android-api.herokuapp.com/get_users"),
    CREATEUSER ("https://codelocker-android-api.herokuapp.com/create_users"),
    USERCOUNT ("https://codelocker-android-api.herokuapp.com/user_count"),
    GETPASSWORD ("https://codelocker-android-api.herokuapp.com/password"),
    USERNAMEOCCURRENCE ("https://codelocker-android-api.herokuapp.com/username_occurrence"),
    EMAILOCCURRENCE ("https://codelocker-android-api.herokuapp.com/email_occurrence"),
    USERID ("https://codelocker-android-api.herokuapp.com/uuid"),
    ADDCREDENTIALS("https://codelocker-android-api.herokuapp.com/add_credentials"),
    CREDENTIALSCOUNT ("https://codelocker-android-api.herokuapp.com/credentials_count"),
    CREDENTIALS ("https://codelocker-android-api.herokuapp.com/credentials");

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
