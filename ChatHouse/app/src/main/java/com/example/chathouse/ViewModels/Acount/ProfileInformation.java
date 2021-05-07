package com.example.chathouse.ViewModels.Acount;

import java.util.ArrayList;

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
    private ArrayList<RoomModel> createdRooms;
    private ArrayList<RoomModel> inRooms;


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

    public ArrayList<RoomModel> getCreatedRooms() {
        return createdRooms;
    }

    public ArrayList<RoomModel> getInRooms() {
        return inRooms;
    }
}
