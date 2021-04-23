package com.example.chathouse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.Pages.MainActivity;
import com.example.chathouse.Pages.ProfilePage;
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
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        StartRoom = (Button) findViewById(R.id.StartRoom);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        loading.setVisibility(View.INVISIBLE);


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

        CreateRoomViewModel Room = RoomModel("test", "test", null, null);
        Call<GetRoomViewModel> CreateRoom = APIS.CreateRoom(Room);
        StartRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Open(v);

//                loading.setVisibility(View.VISIBLE);
//                CreateRoom.enqueue(new Callback<GetRoomViewModel>() {
//                    @Override
//                    public void onResponse(Call<GetRoomViewModel> call, Response<GetRoomViewModel> response) {
//                        if(!response.isSuccessful()){
//                            try {
//                                loading.setVisibility(View.INVISIBLE);
//                                System.out.println("1" + response.errorBody().string());
//                                System.out.println("1" + response.code());
//                                System.out.println(response.errorBody().string());
//                            } catch (IOException e) {
//                                loading.setVisibility(View.INVISIBLE);
//                                System.out.println("2" + response.errorBody().toString());
//
//                                e.printStackTrace();
//                            }
//                            return;
//                        }
//                        loading.setVisibility(View.INVISIBLE);
//                        System.out.println("Room just Created Okay" + response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<GetRoomViewModel> call, Throwable t) {
//                        loading.setVisibility(View.INVISIBLE);
//                        Toast.makeText(HomePage.this, "Request failed", Toast.LENGTH_LONG).show();
//                    }
//                });
            }
        });
    }

    private CreateRoomViewModel RoomModel(String name, String description, String startDate, String endDate) {
        CreateRoomViewModel RoomModel = new CreateRoomViewModel(name, description, CreateInterests(), startDate, endDate);
        return RoomModel;
    }

    private ArrayList<ArrayList<Integer>> CreateInterests() {
        ArrayList<ArrayList<Integer>> interests = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            interests.add(new ArrayList<>());
        }

        interests.get(2).add((int) Math.pow(2, 3));

        return interests;
    }

    public void Open(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_room_creation, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
