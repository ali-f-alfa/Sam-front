package com.example.chathouse.ViewModels.Chat;

import com.example.chathouse.ViewModels.Acount.FollowingFollowers;
import com.example.chathouse.ViewModels.Acount.SearchPerson;

public class JoinRoomViewModel {
    private SearchPerson userModel;
    private int roomId;

    public void setUser(SearchPerson user) {
        this.userModel = user;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public SearchPerson getUser() {
        return userModel;
    }

    public int getRoomId() {
        return roomId;
    }
}
