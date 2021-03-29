package com.example.chathouse;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileInformation {

    private String username;
    private String email;
    private Boolean isMe;


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getMe() {
        return isMe;
    }

}
