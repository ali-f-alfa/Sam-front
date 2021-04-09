package com.example.chathouse.ViewModels.Acount;

import java.util.ArrayList;
import java.util.List;

public class UpdateProfileViewModel {
    private String username;
    private String firstName;
    private String lastName;
    private String bio;

    private ArrayList<ArrayList<Integer>> interests;

    public UpdateProfileViewModel(String username, String firstName, String lastName, String bio,  ArrayList<ArrayList<Integer>> interests) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.interests = interests;
    }
}
