package com.example.chathouse.ViewModels.Acount;

public class OutputSignupViewModel {
    private String Username;
    private String Email;
    private String Password;
    private String ConfirmationPassword;

    public OutputSignupViewModel(String username, String email, String password, String confirmationPassword) {
        Username = username;
        Email = email;
        Password = password;
        ConfirmationPassword = confirmationPassword;
    }
}
