package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.GetRoomViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Room extends AppCompatActivity {
    private Button Leave;
    private TextView RoomName;
    private GetRoomViewModel RoomInfo;
    private String Creator;
    private String Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Bundle bundle = getIntent().getExtras();

        Leave = (Button)findViewById(R.id.LeaveRoom);
        RoomName = (TextView)findViewById(R.id.RoomName);

        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        String Token = settings.getString("Token", "n/a");
        String Username = settings.getString("Username", "n/a");

        int RoomId = bundle.getInt("RoomId");




        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", Token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUrl.get(Constants.baseURL))
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory((GsonConverterFactory.create(gson)))
                .build();


        ChatHouseAPI APIS = retrofit.create(ChatHouseAPI.class);

        Call<Void> LeaveRoom = APIS.LeaveRoom(RoomId);
        Call<GetRoomViewModel> GetRoom = APIS.GetRoom(RoomId);
        GetRoom.enqueue(new Callback<GetRoomViewModel>() {
            @Override
            public void onResponse(Call<GetRoomViewModel> call, retrofit2.Response<GetRoomViewModel> response) {
                if(!response.isSuccessful()){
                    try {
                        System.out.println("1" + response.errorBody().string());
                        System.out.println("1" + response.code());
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        System.out.println("2" + response.errorBody().toString());
                        e.printStackTrace();
                    }
                    return;
                }
                GetRoomViewModel Response = response.body();
                Creator = Response.getCreator().getUsername();
                Name = Response.getName();
                RoomName.setText(Name);

                if(Creator.equals(Username)){
                    Leave.setVisibility(View.GONE);
                }
                System.out.println("Works");

            }

            @Override
            public void onFailure(Call<GetRoomViewModel> call, Throwable t) {
                Toast.makeText(Room.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        RoomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Room.this, com.example.chathouse.Pages.RoomInfo.class);
                Bundle bundle = new Bundle();
                bundle.putInt("RoomId", RoomId);
                bundle.putString("Creator", Creator);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeaveRoom.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                        if(!response.isSuccessful()){
                            try {
                                System.out.println("1" + response.errorBody().string());
                                System.out.println("1" + response.code());
                                System.out.println(response.errorBody().string());
                            } catch (IOException e) {
                                System.out.println("2" + response.errorBody().toString());
                                e.printStackTrace();
                            }
                            return;
                        }
                        System.out.println("Deleted");
                        finish();

                        Intent intent = new Intent(Room.this, AcitivityPage.class);

                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(Room.this, "Request failed" , Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}