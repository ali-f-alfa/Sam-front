package com.example.chathouse.ViewModels.Acount;

public class SearchPerson {
    private String username;
    private String imageLink;
    private String firstName;
    private String lastName;


    public SearchPerson(String userName, String ImageLink, String firstName, String lastName) {
        this.username = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageLink = ImageLink;
    }

    public String getUsername() {
        return username;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
