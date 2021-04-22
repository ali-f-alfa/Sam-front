package com.example.chathouse.ViewModels.Acount;

import java.io.Serializable;
import java.util.ArrayList;

public class FollowingFollowers implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private String imageLink;
    private ArrayList<Room> CreatedRooms;
    private ArrayList<Room> InRooms;

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getImageLink() { return imageLink; }

}
