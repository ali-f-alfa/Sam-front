package com.example.chathouse.ViewModels;

import java.util.ArrayList;

public class CreateRoomViewModel {
    private String name;
    private String description;
    private ArrayList<ArrayList<Integer>> interests;
    private String startDate;
    private String endDate;

    public CreateRoomViewModel(String name, String description, ArrayList<ArrayList<Integer>> interests, String startDate, String endDate) {
        this.name = name;
        this.description = description;
        this.interests = interests;
        this.startDate = startDate;
        this.endDate = endDate;
    }


}
