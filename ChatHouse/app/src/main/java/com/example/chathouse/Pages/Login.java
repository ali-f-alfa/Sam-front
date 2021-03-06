package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.ViewModels.Acount.OutputLoginViewModel;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Login extends AppCompatActivity {

    private EditText Username;
    private EditText Password;
    private Button LoginButton;
    private TextView Error;
    private TextView SignUp;
    public String Token;
    private ProgressBar Load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings  = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String themeName = settings.getString("ThemeName", "Theme");
        if (themeName.equalsIgnoreCase("DarkTheme")) {
            setTheme(R.style.DarkTheme_ChatHouse);
        } else if (themeName.equalsIgnoreCase("Theme")) {
            setTheme(R.style.Theme_ChatHouse);
        }
        setContentView(R.layout.activity_login);
        Username = (EditText)findViewById(R.id.Username);
        Password = (EditText)findViewById(R.id.Password);
        LoginButton = (Button)findViewById(R.id.Button);
        Error  = (TextView)findViewById(R.id.ErrorMessage);
        SignUp = (TextView)findViewById(R.id.Signup);
        Load = (ProgressBar)findViewById(R.id.progressBar);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory((GsonConverterFactory.create(gson)))
                .build();
        ChatHouseAPI LoginAPI = retrofit.create(ChatHouseAPI.class);


        Load.setVisibility(View.GONE);

        // Send request for login
        LoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(CheckFields()){
                    // Class for login body
                    OutputLoginViewModel Body = new OutputLoginViewModel(Username.getText().toString(),
                            Password.getText().toString(), CheckUserNamePattern(Username.getText().toString()));
                    Call<String> Login = LoginAPI.PostLogin(Body);

                    Error.setText("");
                    Load.setVisibility(View.VISIBLE);
                    Login.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response){
                            if(!response.isSuccessful()){
                                if (response.code() == 401)
                                    Error.setText("Email is not confirmed");
//                                    Toast.makeText(Login.this, "Email is not confirmed", Toast.LENGTH_LONG).show();

                                else if (response.code() == 423)
                                    Error.setText("Too many Failed attempts! please try later.");
//                                    Toast.makeText(Login.this, "Too many Failed attempts! please try later.", Toast.LENGTH_LONG).show();

                                else if (response.code() == 400)
//                                    Toast.makeText(Login.this, "Inavlid login attempt", Toast.LENGTH_LONG).show();
                                    Error.setText("Invalid login attempt");

                                else if (response.code() == 404)
//                                    Toast.makeText(Login.this, "Email or Username is Not Valid", Toast.LENGTH_LONG).show();
                                    Error.setText("Email or Username is Not Valid");

                                Load.setVisibility(View.INVISIBLE);

                                return;
                            }
                            // Get Profile
//                            Toast.makeText(Login.this, "Successfully Logged In ", Toast.LENGTH_LONG).show();
                            Token = response.body();

                            Load.setVisibility(View.INVISIBLE);

                            SharedPreferences.Editor edit = getSharedPreferences("Storage", MODE_PRIVATE).edit();
                            edit.putString("Token", Token);
                            edit.putString("Username", Username.getText().toString());
                            edit.apply();
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Login.this, ProfilePage.class);
                                    Bundle bundle = new Bundle();

                                    bundle.putString("Token", Token);
                                    bundle.putString("Username", Username.getText().toString());

                                    intent.putExtras(bundle);

                                    startActivity(intent);
                                    finish();
                                }
                            });



                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable failure){
//                            Error.setText("please check your connection");
                            Toast.makeText(Login.this, "Please check your connection", Toast.LENGTH_LONG).show();
                            Load.setVisibility(View.INVISIBLE);}
                    });
                }
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Sign-Up screen
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Login.this, com.example.chathouse.Pages.SignUp.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    private Boolean CheckFields(){
        Boolean CheckUsername = true;
        Boolean CheckPassword = true;
        // Check Username
        if(!TextUtils.isEmpty(Username.getText().toString())){
            // Check the pattern
            CheckUsername = true;
        }
        else {
            CheckUsername = false;
            Username.setError("Email or Username is required");
        }

        // Check Password
        if(!TextUtils.isEmpty(Password.getText().toString())){
            // Match patterns
            CheckPassword = CheckPasswordPattern(Password.getText().toString());
        }
        else{
            CheckPassword = false;
            Password.setError("Password is required");
        }

        return (CheckPassword && CheckUsername);
    }

    private Boolean CheckUserNamePattern(String username){

        // If Email
        Boolean email = Patterns.EMAIL_ADDRESS.matcher(username).matches();

        if(email){
            return true;
        }
        return false;
    }

    private Boolean CheckPasswordPattern(String password){
        Boolean pass = true;
        // It should be more than 3 characters
        if(password.length() < 3){
            Password.setError("Should be at least 3 characters");
            return false;
        }
        char[] passwordChars = password.toCharArray();
        for(char ch : passwordChars){
            if(!Constants.PasswordAllowedCharacters.contains(Character.toString(ch)))
                pass = false;
        }

        return pass;
    }
}