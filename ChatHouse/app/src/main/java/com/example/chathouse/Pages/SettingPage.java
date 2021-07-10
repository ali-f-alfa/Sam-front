package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SettingPage extends AppCompatActivity {
    private Button LogoutButton;
    private ChatHouseAPI API;
    SharedPreferences settings;
    RadioButton defaultTheme, darkTheme;
    RadioGroup group;
    String themeName;
    ConstraintLayout layout;
    String U;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings  = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String themeName = settings.getString("ThemeName", "Theme");

        if (themeName.equalsIgnoreCase("DarkTheme")) {
            setTheme(R.style.DarkTheme_ChatHouse);

        } else if (themeName.equalsIgnoreCase("Theme")) {
            setTheme(R.style.Theme_ChatHouse);

        }
        setContentView(R.layout.activity_setting_page);
        LogoutButton =  (Button)findViewById(R.id.LogoutButton);
        settings = getSharedPreferences("Storage", MODE_PRIVATE);
        layout = (ConstraintLayout)findViewById(R.id.settingback);

        U = getIntent().getExtras().getString("Username");
        group = (RadioGroup) findViewById(R.id.group);
        defaultTheme = (RadioButton) findViewById(R.id.light);
        darkTheme = (RadioButton) findViewById(R.id.dark);
        if (themeName.equalsIgnoreCase("DarkTheme")) {
            layout.setBackgroundResource(R.drawable.b22d);

        } else if (themeName.equalsIgnoreCase("Theme")) {
            layout.setBackgroundResource(R.drawable.b22);
        }



        if (themeName.equalsIgnoreCase("DarkTheme")) {
            darkTheme.setChecked(true);

        } else if (themeName.equalsIgnoreCase("Theme")) {
            defaultTheme.setChecked(true);

        }

        // Called when the checked radio button has changed.
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.light) {
                    setTheme("Theme");
                } else if (checkedId == R.id.dark) {
                    setTheme("DarkTheme");
                }
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = getIntent();
                        finish();
                        Intent i = new Intent(SettingPage.this, ProfilePage.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("Username", U);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });

            }
        });

        String Token = settings.getString("Token", "n/a");
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", Token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUrl.get(Constants.baseURL))
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory((GsonConverterFactory.create(gson)))
                .build();
        API = retrofit.create(ChatHouseAPI.class);


        Call<Void> Logout = API.PostLogout();
        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout Request
                Logout.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(!response.isSuccessful()){
                            try {
                                System.out.println(response.errorBody().string());
                            } catch (IOException e) {
                                System.out.println(response.errorBody().toString());
                                e.printStackTrace();
                            }
                            return;
                        }

                        SharedPreferences.Editor edit = getSharedPreferences("Storage", MODE_PRIVATE).edit();
                        edit.remove("Token");
                        edit.apply();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run () {
                                Intent intent = new Intent(SettingPage.this, Login.class);
                                finishAffinity();
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(SettingPage.this, "Request failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    public void setTheme(String name) {
        // Create preference to store theme name
        SharedPreferences preferences = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ThemeName", name);
        editor.apply();
        recreate();
    }
}