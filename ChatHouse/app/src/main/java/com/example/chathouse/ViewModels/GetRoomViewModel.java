package com.example.chathouse.ViewModels;

import java.util.ArrayList;

public class GetRoomViewModel {
    private String name;
    private String description;
    private ArrayList<ArrayList<Integer>> interests;
    private String startDate;
    private String endDate;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<ArrayList<Integer>> getInterests() {
        return interests;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
