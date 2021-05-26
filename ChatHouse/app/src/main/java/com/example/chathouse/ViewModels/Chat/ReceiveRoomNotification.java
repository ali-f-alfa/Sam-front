package com.example.chathouse.ViewModels.Chat;

import com.example.chathouse.ViewModels.Acount.SearchPerson;

public class ReceiveRoomNotification {
    public int notification;
    public SearchPerson userModel;
    public int roomId;
    public Boolean isMe;

    public int getNotification() {
        return notification;
    }

    public SearchPerson getUserModel() {
        return userModel;
    }

    public int getRoomId() {
        return roomId;
    }

    public Boolean getMe() {
        return isMe;
    }

}


