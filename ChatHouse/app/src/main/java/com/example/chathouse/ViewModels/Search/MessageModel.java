package com.example.chathouse.ViewModels.Search;

import com.example.chathouse.ViewModels.Acount.SearchPerson;

public class MessageModel {
    private SearchPerson userModel;
    private Object message;
    private int roomId;
    private boolean isMe;
    private MessageType messageType = MessageType.Text;

}

enum MessageType
{
    Text,
    File,
    JoinNotification,
    LeftNotification
}