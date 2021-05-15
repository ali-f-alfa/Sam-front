package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.InputSignupViewModel;
import com.example.chathouse.ViewModels.Acount.OutputSignupViewModel;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {
    private TextView textViewResult;
    private EditText emailTextView;
    private EditText userNameTextView;
    private EditText passwordTextView;
    private EditText confirmPasswordTextView;
    private Button SubmitButton;
    private TextView Login;
    private ProgressBar Load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseURL)
                .addConverterFactory((GsonConverterFactory.create()))
                .build();
        ChatHouseAPI chatHouseAPI = retrofit.create(ChatHouseAPI.class);


        textViewResult = findViewById(R.id.resultText);
        emailTextView = findViewById(R.id.EmailInput);
        userNameTextView = findViewById(R.id.usernameInput);
        passwordTextView = findViewById(R.id.passwordInput);
        confirmPasswordTextView = findViewById(R.id.confirmPasswordInput);
        SubmitButton = findViewById(R.id.submitBtn);
        Login = findViewById(R.id.LoginBtn);
        Load = (ProgressBar)findViewById(R.id.progressBar);
        Load.setVisibility(View.GONE);

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Load.setVisibility(View.VISIBLE);

                if (checkFields()) {
                    OutputSignupViewModel model1 = new OutputSignupViewModel(userNameTextView.getText().toString(),
                            emailTextView.getText().toString(),
                            passwordTextView.getText().toString(),
                            confirmPasswordTextView.getText().toString());
                    Call<InputSignupViewModel> signup = chatHouseAPI.postSignup(model1);

                    //todo: loading
                    signup.enqueue(new Callback<InputSignupViewModel>() {
                        @Override
                        public void onResponse(Call<InputSignupViewModel> call, Response<InputSignupViewModel> response) {
                            if (!response.isSuccessful()) {
                                try {
                                    textViewResult.setTextColor(Color.parseColor("#B00020"));
                                    textViewResult.setText(response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                            Toast.makeText(SignUp.this, "Successfully Signed Up", Toast.LENGTH_LONG).show();
                            Load.setVisibility(View.INVISIBLE);

                            textViewResult.setTextColor(Color.BLACK);
                            textViewResult.setText("We have sent an email with a confirmation link to " + response.body().getEmail());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SignUp.this, com.example.chathouse.Pages.Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 3000);
                        }

                        @Override
                        public void onFailure(Call<InputSignupViewModel> call, Throwable t) {
                            textViewResult.setTextColor(Color.parseColor("#B00020"));
                            textViewResult.setText("please check your connection");
                        }
                    });
//                    textViewResult.append("wait... ");
                }
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SignUp.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    private boolean checkFields() {
        boolean hasEmpty = false;
        if (TextUtils.isEmpty(emailTextView.getText())) {
            emailTextView.setError("Email is required!");
            hasEmpty = true;
        }
        if (TextUtils.isEmpty(userNameTextView.getText())) {
            userNameTextView.setError("Username is required!");
            hasEmpty = true;
        }
        if (TextUtils.isEmpty(passwordTextView.getText())) {
            passwordTextView.setError("Password is required!");
            hasEmpty = true;
        }
        if (TextUtils.isEmpty(confirmPasswordTextView.getText())) {
            confirmPasswordTextView.setError("Confirm Password is required!");
            hasEmpty = true;
        }
        if (hasEmpty == true)
            return false;

        boolean isValid = true;
        if (!isValidEmail(emailTextView.getText().toString())) {
            emailTextView.setError("Email is not in Valid format");
            isValid = false;
        }
        if (!isValidUsername(userNameTextView.getText().toString())) {
            userNameTextView.setError("Username is not in Valid format");
            isValid = false;
        }
        if (!CheckPassword(passwordTextView.getText().toString(), confirmPasswordTextView.getText().toString())) {
            isValid = false;
        }
        if (isValid == false)
            return false;

        return true;
    }

    private boolean CheckPassword(String pass, String confirmPass) {
        for (char ch : pass.toCharArray()) {
            if (!Constants.PasswordAllowedCharacters.contains(Character.toString(ch))) {
                passwordTextView.setError("Password contains unallowed characters");
                return false;
            }
        }
        if (!pass.equals(confirmPass)) {
            passwordTextView.setError("password and confirm password not match");
            confirmPasswordTextView.setError("password and confirm password not match");
            return false;
        }

        return true;
    }

    private boolean isValidUsername(String username) {
        char[] usernameChars = username.toCharArray();
        for (char ch : usernameChars) {
            if (!Constants.UsernameAllowedCharacters.contains(Character.toString(ch)))
                return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}