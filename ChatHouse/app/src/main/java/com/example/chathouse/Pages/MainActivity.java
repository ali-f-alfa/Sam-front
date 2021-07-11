package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.chathouse.Pages.HomePage;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;

public class MainActivity extends AppCompatActivity {
    SharedPreferences memory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memory  = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String themeName = memory.getString("ThemeName", "Theme");
        if (themeName.equalsIgnoreCase("DarkTheme")) {
            setTheme(R.style.DarkTheme_ChatHouse);
        } else if (themeName.equalsIgnoreCase("Theme")) {
            setTheme(R.style.Theme_ChatHouse);
        }
        setContentView(R.layout.activity_main);
        memory = getSharedPreferences("Storage", MODE_PRIVATE);
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