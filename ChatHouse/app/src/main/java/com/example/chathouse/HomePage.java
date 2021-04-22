package com.example.chathouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.CreateRoomViewModel;
import com.example.chathouse.ViewModels.GetRoomViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

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

public class HomePage extends AppCompatActivity {

    private Button StartRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        StartRoom = (Button)findViewById(R.id.StartRoom);

        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        SharedPreferences.Editor edit = getSharedPreferences("Storage", MODE_PRIVATE).edit();


        String Token = settings.getString("Token", "n/a");


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
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUrl.get(Constants.baseURL))
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory((GsonConverterFactory.create(gson)))
                .build();
        ChatHouseAPI APIS = retrofit.create(ChatHouseAPI.class);

        CreateRoomViewModel Room = RoomModel("", "", null, "", "");
        Call<GetRoomViewModel> CreateRoom = APIS.CreateRoom(Room);
        CreateRoom.enqueue(new Callback<GetRoomViewModel>() {
            @Override
            public void onResponse(Call<GetRoomViewModel> call, Response<GetRoomViewModel> response) {

            }

            @Override
            public void onFailure(Call<GetRoomViewModel> call, Throwable t) {

            }
        });
    }

    private CreateRoomViewModel RoomModel(String name, String description, ArrayList<ArrayList<Integer>> interests, String startDate,String endDate){
        CreateRoomViewModel RoomModel = new CreateRoomViewModel(name, description, interests, startDate, endDate);
        return RoomModel;
    }
}