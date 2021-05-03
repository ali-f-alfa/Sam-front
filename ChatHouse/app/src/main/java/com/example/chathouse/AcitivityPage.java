package com.example.chathouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.example.chathouse.Pages.Search;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AcitivityPage extends AppCompatActivity {

    public String Username;
    private BottomNavigationView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acitivity_page);
        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        Username = settings.getString("Username", "n/a");
        menu = (BottomNavigationView) findViewById(R.id.Activity_menu);

        menu.setOnNavigationItemSelectedListener(navListener);
        int a = menu.getSelectedItemId();


    }
    public BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            item.setEnabled(false);
            switch (item.getItemId()) {
                case R.id.nav_home:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.Pages.HomePage.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                    break;


                case R.id.nav_Profile:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.Pages.ProfilePage.class);
                            Bundle bundle = new Bundle();


                            bundle.putString("Username", Username);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;


                case R.id.nav_Search:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.Pages.Search.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;

                case R.id.nav_Activity:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.AcitivityPage.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;
            }
            return false;
        }
    };
}