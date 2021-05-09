package com.example.chathouse.ViewModels.Acount;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.chathouse.Pages.Search;

import java.util.ArrayList;
import java.util.Date;

public class RoomModel {
    private int id;
    private ArrayList<SearchPerson> members;
    private SearchPerson creator;
    private String name;
    private String description;
    private @Nullable Date startDate;
    private @Nullable Date endDate;
    private ArrayList<ArrayList<Integer>> interests;

    public int getRoomId() {
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

    public Date getEndDate() {
        return endDate;
    }

    public void setRoomId(int roomId) {
        this.id = roomId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(@Nullable Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(@Nullable Date endDate) {
        this.endDate = endDate;
    }


    public ArrayList<SearchPerson> getMembers() {
        return members;
    }

    public SearchPerson getCreator() {
        return creator;
    }

    public void setInterests(ArrayList<ArrayList<Integer>> interests) {
        this.interests = interests;
    }

    public ArrayList<ArrayList<Integer>> getInterests() {
        return interests;
    }

    public RoomModel(Integer roomId, String name, String description, Date startDate, Date endDate, ArrayList<ArrayList<Integer>> interests, SearchPerson creator, ArrayList<SearchPerson> members) {
        this.id = roomId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.interests = interests;
        this.members = members;
        this.creator = creator;
    }
}
