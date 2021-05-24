package com.example.chathouse.ViewModels;

import com.example.chathouse.ViewModels.Acount.SearchPerson;

public class MessageViewModel {
    private SearchPerson userModel;
    private Object message;
    private int roomId;
    private boolean isMe;
    private MessageType messageType = MessageType.Text;
    public int parentId;


    public void setUserModel(SearchPerson userModel) {
        this.userModel = userModel;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setMe(boolean me) {
        isMe = me;
    }


    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}

enum MessageType
{
    Text,
    File,
    JoinNotification,
    LeftNotification
}