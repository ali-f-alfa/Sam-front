package com.example.chathouse.ViewModels;

import com.example.chathouse.ViewModels.Acount.SearchPerson;

public class MessageViewModel {
    private SearchPerson userModel;
    private Object message;
    private int roomId;
    private boolean isMe;
    private int parentId;
    private MessageType messageType;
}

enum MessageType
{
    Text,
    File,
    JoinNotification,
    LeftNotification
}