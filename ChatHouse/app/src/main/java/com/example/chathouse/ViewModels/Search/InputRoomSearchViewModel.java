package com.example.chathouse.ViewModels.Search;

import java.util.ArrayList;
import java.util.Date;

public class InputRoomSearchViewModel {
    private int id;
    private String name;
    private String description;
    private Date startDate;
    private ArrayList<ArrayList<Integer>> interests;
    private int membersCount;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public ArrayList<ArrayList<Integer>> getInterests() {
        return interests;
    }

    public int getMembersCount() {
        return membersCount;
    }
}
