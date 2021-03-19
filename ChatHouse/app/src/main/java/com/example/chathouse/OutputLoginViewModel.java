package com.example.chathouse;

public class OutputLoginViewModel{
    private String identifier;
    private String password;
    private Boolean isEmail;

    public OutputLoginViewModel(String username, String Password, Boolean IsEmail){
        identifier = username;
        password = Password;
        isEmail = IsEmail;
    }
}