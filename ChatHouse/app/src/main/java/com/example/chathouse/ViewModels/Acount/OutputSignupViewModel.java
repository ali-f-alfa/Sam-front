package com.example.chathouse.ViewModels.Acount;

public class OutputSignupViewModel {
    private String FirstName;
    private String LastName;
    private String Username;
    private String Email;
    private String Password;
    private String ConfirmationPassword;

    public OutputSignupViewModel(String firstName, String lastName, String username, String email, String password, String confirmationPassword) {
        FirstName = firstName;
        LastName = lastName;
        Username = username;
        Email = email;
        Password = password;
        ConfirmationPassword = confirmationPassword;
    }
}
