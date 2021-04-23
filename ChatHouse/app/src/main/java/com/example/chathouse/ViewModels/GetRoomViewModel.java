package com.example.chathouse.ViewModels;

import com.example.chathouse.ViewModels.Acount.ProfileInformation;

import java.util.ArrayList;

public class GetRoomViewModel {
    private Integer id;
    private ArrayList<String> members;
    private String creator;
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

    public Integer getId() {
        return id;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getCreator() {
        return creator;
    }
}
