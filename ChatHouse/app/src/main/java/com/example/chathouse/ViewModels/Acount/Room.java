package com.example.chathouse.ViewModels.Acount;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Date;

public class Room {
    private Integer roomId;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private ArrayList<ArrayList<Integer>> interests;

    public Integer getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public ArrayList<ArrayList<Integer>> getInterests() {
        return interests;
    }
}
