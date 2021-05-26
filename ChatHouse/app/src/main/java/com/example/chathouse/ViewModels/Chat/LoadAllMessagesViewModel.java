package com.example.chathouse.ViewModels.Chat;

import com.example.chathouse.ViewModels.Acount.SearchPerson;

import java.util.Date;

public class LoadAllMessagesViewModel {
    public int id ;
    public int parentId ;
    public String content ;
    public int contetntType ;
    public Date sentDate ;
    public SearchPerson sender ;
    public Boolean isMe ;
    public int roomId ;

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public String getContent() {
        return content;
    }

    public int getContetntType() {
        return contetntType;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public SearchPerson getSender() {
        return sender;
    }

    public Boolean getMe() {
        return isMe;
    }

    public int getRoomId() {
        return roomId;
    }
}
