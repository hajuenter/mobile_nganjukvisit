package com.polije.sem3.network;

public class Config {
    public static final String BASE_URL = "http://172.16.106.132"; // Your local IP Address
//    public static final String BASE_URL = "https://nganjukvisit.tifnganjuk.com"; // Your local IP Address
//    public static final String API_DIR = "/controllers/user_profiles";
    public static final String API_DIR = "/nganjukvisit/user_profiles";
    public static final String API_UPLOAD = BASE_URL + API_DIR + "/upload.php";
    public static final String API_IMAGE = BASE_URL + API_DIR + "/";
}
