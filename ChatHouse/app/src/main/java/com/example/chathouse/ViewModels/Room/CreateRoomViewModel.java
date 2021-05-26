package com.example.chathouse.ViewModels.Room;

import java.util.ArrayList;
import java.util.Date;

public class CreateRoomViewModel {
    private String name;
    private String description;
    private ArrayList<ArrayList<Integer>> interests;
    private Date startDate;
    private Date endDate;

    public CreateRoomViewModel(String name, String description, ArrayList<ArrayList<Integer>> interests, Date startDate, Date endDate) {
        this.name = name;
        this.description = description;
        this.interests = interests;
        this.startDate = startDate;
        this.endDate = endDate;
    }


}
