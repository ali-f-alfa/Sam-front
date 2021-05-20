package com.example.chathouse.ViewModels;

import com.example.chathouse.ViewModels.Acount.SearchPerson;

public class JoinHubViewModel {
    private SearchPerson user;
    private int roomId;

    public void setUser(SearchPerson user) {
        this.user = user;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
