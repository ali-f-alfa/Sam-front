package com.example.chathouse.Utility;

import androidx.annotation.Nullable;

public class Constants {
    public static final String PasswordAllowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._@+";;
    public static final String UsernameAllowedCharacters  = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    public static final int SPLASH_TIME_OUT = 3000;
    public static final String baseURL = "http://45.82.139.208:5000/api/"; //http://10.0.2.2:13524/api/
    public static final @Nullable  String serverUrl = "http://45.82.139.208:5000/Hubs/ChatRoom"; // connect to signalr server
}
