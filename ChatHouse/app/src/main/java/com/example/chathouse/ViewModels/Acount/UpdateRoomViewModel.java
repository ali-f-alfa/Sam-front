package com.example.chathouse.ViewModels.Acount;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class UpdateRoomViewModel {
    private int roomId;
    private String name;
    private String description;
    private @Nullable
    Date startDate;
    private @Nullable Date endDate;
    private ArrayList<ArrayList<Integer>> interests;

    public int getRoomId() {
        return roomId;
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
        this.roomId = roomId;
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

    public void setInterests(ArrayList<ArrayList<Integer>> interests) {
        this.interests = interests;
    }

    public ArrayList<ArrayList<Integer>> getInterests() {
        return interests;
    }

    public UpdateRoomViewModel(Integer roomId, String name, String description, Date startDate, Date endDate, ArrayList<ArrayList<Integer>> interests) {
        this.roomId = roomId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.interests = interests;
    }
}
