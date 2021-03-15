package com.example.chathouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText Username = (EditText)findViewById(R.id.Username);
        EditText Password = (EditText)findViewById(R.id.Password);
        Button LoginButton = (Button)findViewById(R.id.Button);
        TextView Error  = (TextView)findViewById(R.id.ErrorMessage);
        TextView SignUp = (TextView)findViewById(R.id.Signup);

        Error.setVisibility(View.INVISIBLE);

        // Send request for login
        LoginButton.setOnClickListener(new View.OnClickListener(){
            // Conditions for username

            // Conditions for password

            @Override
            public void onClick(View v){
                // True -> GetProfile
                if(2 > 1){
                    Error.setVisibility(View.INVISIBLE);
                }
                // False -> Error
                else{
                    Error.setVisibility(View.VISIBLE);
                }
            }
        });
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Sign-Up screen
                Error.setVisibility(View.VISIBLE);
            }
        });
    }
}