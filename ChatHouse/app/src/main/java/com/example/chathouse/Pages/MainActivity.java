package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.chathouse.HomePage;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences memory = getSharedPreferences("Storage", MODE_PRIVATE);
        String Token = memory.getString("Token", null);

        if(Token != null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Username", memory.getString("Username", "n/a"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }, Constants.SPLASH_TIME_OUT);
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, Constants.SPLASH_TIME_OUT);

        }
    }
}