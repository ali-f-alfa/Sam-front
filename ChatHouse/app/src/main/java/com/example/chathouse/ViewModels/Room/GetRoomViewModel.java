package com.example.chathouse.ViewModels.Room;

import androidx.annotation.Nullable;

import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.SearchPerson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class GetRoomViewModel implements Serializable {
    private int id;
    private ArrayList<SearchPerson> members;
    private SearchPerson creator;
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

    public ArrayList<SearchPerson> getMembers() {
        return members;
    }

    public SearchPerson getCreator() {
        return creator;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMembers(ArrayList<SearchPerson> members) {
        this.members = members;
    }

    public void setCreator(SearchPerson creator) {
        this.creator = creator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInterests(ArrayList<ArrayList<Integer>> interests) {
        this.interests = interests;
    }

    public void setStartDate(@Nullable Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(@Nullable Date endDate) {
        this.endDate = endDate;
    }
}
