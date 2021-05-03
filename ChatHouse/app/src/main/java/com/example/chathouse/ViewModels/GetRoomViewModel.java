package com.example.chathouse.ViewModels;

import androidx.annotation.Nullable;

import com.example.chathouse.ViewModels.Acount.ProfileInformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class GetRoomViewModel implements Serializable {
    private int id;
    private ArrayList<String> members;
    private String creator;
    private String name;
    private String description;
    private ArrayList<ArrayList<Integer>> interests;
    private @Nullable Date startDate;
    private @Nullable Date endDate;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<ArrayList<Integer>> getInterests() {
        return interests;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getId() {
        return id;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getCreator() {
        return creator;
    }
}
