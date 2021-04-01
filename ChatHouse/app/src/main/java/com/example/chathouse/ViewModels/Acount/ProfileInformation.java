package com.example.chathouse.ViewModels.Acount;

import com.example.chathouse.ViewModels.Acount.FollowingFollowers;
import com.example.chathouse.ViewModels.Acount.Interests;

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
    private Interests interests;


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
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

    public List<FollowingFollowers> getFollowers() {
        return followers;
    }

    public List<FollowingFollowers> getFollowings() {
        return followings;
    }

    public Interests getInterests() {
        return interests;
    }
}
