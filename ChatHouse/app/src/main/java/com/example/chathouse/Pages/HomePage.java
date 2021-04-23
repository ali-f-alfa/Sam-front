package com.example.chathouse.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.CreateRoomViewModel;
import com.example.chathouse.ViewModels.GetRoomViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private Call<GetRoomViewModel> CreateRoom;
    private ChatHouseAPI APIS;
    BottomNavigationView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        StartRoom = (Button) findViewById(R.id.StartRoom);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        loading.setVisibility(View.INVISIBLE);
        menu = (BottomNavigationView) findViewById(R.id.Home_menu);


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
        APIS = retrofit.create(ChatHouseAPI.class);

        menu.setOnNavigationItemSelectedListener(navListener);
        int a = menu.getSelectedItemId();

        CreateRoomViewModel Room = RoomModel("test", "test", null, null);
        Call<GetRoomViewModel> CreateRoom = APIS.CreateRoom(Room);
        StartRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Open(v);


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

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_room_creation);


        Button dialogButton = (Button) dialog.findViewById(R.id.Create);
        // if button is clicked, close the custom dialog
        EditText name = (EditText)dialog.findViewById(R.id.editNameRoom);
        EditText description = (EditText)dialog.findViewById(R.id.editDesRoom);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateRoomViewModel Room = RoomModel(name.getText().toString(), description.getText().toString(), null, null);
                CreateRoom = APIS.CreateRoom(Room);
                loading.setVisibility(View.VISIBLE);
                CreateRoom.enqueue(new Callback<GetRoomViewModel>() {
                    @Override
                    public void onResponse(Call<GetRoomViewModel> call, Response<GetRoomViewModel> response) {
                        if(!response.isSuccessful()){
                            try {
                                loading.setVisibility(View.INVISIBLE);
                                System.out.println("1" + response.errorBody().string());
                                System.out.println("1" + response.code());
                                System.out.println(response.errorBody().string());
                            } catch (IOException e) {
                                loading.setVisibility(View.INVISIBLE);
                                System.out.println("2" + response.errorBody().toString());

                                e.printStackTrace();
                            }
                            return;
                        }
                        loading.setVisibility(View.INVISIBLE);
                        System.out.println("Room just Created Okay" + response.body());
                    }

                    @Override
                    public void onFailure(Call<GetRoomViewModel> call, Throwable t) {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(HomePage.this, "Request failed", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
            String Username = settings.getString("Username", "n/a");

            switch (item.getItemId()) {
                case R.id.nav_home:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(HomePage.this, com.example.chathouse.Pages.HomePage.class);
                            Bundle bundle = new Bundle();


                            bundle.putString("Username", Username);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;


                case R.id.nav_Profile:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(HomePage.this, com.example.chathouse.Pages.ProfilePage.class);
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

                            Intent intent = new Intent(HomePage.this, com.example.chathouse.Pages.Search.class);
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
