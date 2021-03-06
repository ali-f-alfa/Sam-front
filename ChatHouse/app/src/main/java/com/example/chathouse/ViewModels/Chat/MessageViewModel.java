package com.example.chathouse.ViewModels.Chat;

import com.example.chathouse.ViewModels.Acount.SearchPerson;

public class MessageViewModel {
    private SearchPerson userModel;
    private Object message;
    private Integer roomId;
    private boolean isMe;
    private int messageType;
    private int parentId = -1;

    public void setUserModel(SearchPerson userModel) {
        this.userModel = userModel;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public void setMe(boolean me) {
        isMe = me;
    }


    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public SearchPerson getUserModel() {
        return userModel;
    }

    public Object getMessage() {
        return message;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean isMe() {
        return isMe;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getParentId() {
        return parentId;
    }


}

