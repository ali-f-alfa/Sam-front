package com.example.chathouse.ViewModels.Acount;

import com.example.chathouse.ViewModels.Acount.FollowingFollowers;
import com.example.chathouse.ViewModels.Acount.Interests;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProfileInformation {

    private String username;
    private String email;
    private Boolean isMe;
    private String firstName;
    private String lastName;
    private String bio;
    private ArrayList<FollowingFollowers> followers;
    private ArrayList<FollowingFollowers> followings;
    private ArrayList<ArrayList<Integer>> interests;
    private String imageLink;


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getImageLink() {
        return imageLink;
    }

    public Boolean getMe() {
        return isMe;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBio() {
        return bio;
    }

    public ArrayList<FollowingFollowers> getFollowers() {
        return followers;
    }

    public ArrayList<FollowingFollowers> getFollowings() {
        return followings;
    }

    public ArrayList<ArrayList<Integer>> getInterests() {
        return interests;
    }
}
