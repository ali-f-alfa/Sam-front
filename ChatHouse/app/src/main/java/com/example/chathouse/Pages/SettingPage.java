package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        LogoutButton =  (Button)findViewById(R.id.LogoutButton);
        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);


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
}