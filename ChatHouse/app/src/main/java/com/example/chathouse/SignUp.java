package com.example.chathouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:13524/api/")
                .addConverterFactory((GsonConverterFactory.create()))
                .build();
        ChatHouseAPI chatHouseAPI = retrofit.create(ChatHouseAPI.class);


        textViewResult = findViewById(R.id.resultText);
        emailTextView = findViewById(R.id.EmailInput);
        userNameTextView = findViewById(R.id.usernameInput);
        passwordTextView = findViewById(R.id.passwordInput);
        confirmPasswordTextView = findViewById(R.id.confirmPasswordInput);
        Button mButton = findViewById(R.id.submitBtn);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                                    textViewResult.setText(response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                            Toast.makeText(SignUp.this, "Successfully Signed Up", Toast.LENGTH_LONG).show();

                            textViewResult.setText("We have sent an email with a confirmation link to " + response.body().getEmail());
                            //todo : redirect to login page

                        }

                        @Override
                        public void onFailure(Call<InputSignupViewModel> call, Throwable t) {
                            textViewResult.setText("please check your connection");
                        }
                    });
//                    textViewResult.append("wait... ");
                }
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
            if (!Constants.PasswordAllowedUserNameCharacters.contains(Character.toString(ch))) {
                passwordTextView.setError("Password contains unallowed characters");
                return false;
            }
        }
        if (!pass.equals(confirmPass)) {
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