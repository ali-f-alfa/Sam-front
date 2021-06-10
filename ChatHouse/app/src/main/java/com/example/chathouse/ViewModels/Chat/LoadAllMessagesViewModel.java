package com.example.chathouse.ViewModels.Chat;

import com.example.chathouse.ViewModels.Acount.SearchPerson;

import java.util.Date;

public class LoadAllMessagesViewModel {
    public Integer id ;
    public Integer parentId ;
    public String content ;
    public int contetntType ;
    public Date sentDate ;
    public SearchPerson sender ;
    public Boolean isMe ;
    public int roomId ;

    public int getId() {
        return id;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getParentId() {
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
