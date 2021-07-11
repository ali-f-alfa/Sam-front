package com.example.chathouse.ViewModels.Acount;

import java.io.Serializable;
import java.util.ArrayList;

public class FollowingFollowers implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private String imageLink;

    public FollowingFollowers(String userName, String ImageLink, String firstName, String lastName) {
        this.username = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageLink = ImageLink;
    }
    public FollowingFollowers(){

    }

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
