package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Username = (EditText)findViewById(R.id.Username);
        Password = (EditText)findViewById(R.id.Password);
        LoginButton = (Button)findViewById(R.id.Button);
        Error  = (TextView)findViewById(R.id.ErrorMessage);
        SignUp = (TextView)findViewById(R.id.Signup);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:13524/api/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory((GsonConverterFactory.create(gson)))
                .build();
        ChatHouseAPI LoginAPI = retrofit.create(ChatHouseAPI.class);



        // Send request for login
        LoginButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v){
                if(CheckFields()){
                    // Class for login body
                    OutputLoginViewModel Body = new OutputLoginViewModel(Username.getText().toString(),
                            Password.getText().toString(), CheckUserNamePattern(Username.getText().toString()));
                    Call<String> Login = LoginAPI.PostLogin(Body);


                    Login.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response){
                            if(!response.isSuccessful()){
                                try {
                                    Error.setText(response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                            // Get Profile
                            Toast.makeText(Login.this, "Successfully Logged In ", Toast.LENGTH_LONG).show();
                            Error.setText("Your are now logged in");
                            Token = response.body();
                            Intent intent = new Intent(Login.this, ProfilePage.class);
                            Bundle bundle = new Bundle();

                            bundle.putString("Token", Token);
                            bundle.putString("Username", Username.getText().toString());

                            intent.putExtras(bundle);

                            startActivity(intent);

                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable failure){
                            Error.setText("please check your connection");                        }
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
            Password.setError("Should be al least 3 characters");
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