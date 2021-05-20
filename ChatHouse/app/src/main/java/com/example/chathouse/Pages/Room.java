package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.SearchPerson;
import com.example.chathouse.ViewModels.GetRoomViewModel;
import com.example.chathouse.ViewModels.JoinHubViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Single;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
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

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class Room extends AppCompatActivity {
    private Button Leave;
    private TextView RoomName;
    private GetRoomViewModel RoomInfo;
    private String Creator;
    private String Name;
    HubConnection hubConnection; //Do the signalR definitions
    HubProxy hubProxy;
    Handler mHandler=new Handler(); //listener
    com.example.chathouse.ViewModels.JoinHubViewModel JoinHubViewModel = new JoinHubViewModel();
    int RoomId;
    String Token;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.gc();

        // Hub Leave
        Leave();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Bundle bundle = getIntent().getExtras();

        Leave = (Button)findViewById(R.id.LeaveRoom);
        RoomName = (TextView)findViewById(R.id.RoomName);

        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        Token = settings.getString("Token", "n/a");
        String Username = settings.getString("Username", "n/a");

        RoomId = bundle.getInt("RoomId");


        // Hub Join
        Connect();

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

        Call<ProfileInformation> GetProfile = APIS.GetProfile(Username);

        GetProfile.enqueue(new Callback<ProfileInformation>() {
            @Override
            public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                if (!response.isSuccessful()) {
                    try {
                        System.out.println("1" + response.errorBody().string());
                        System.out.println("1" + response.code());
                    } catch (IOException e) {
                        System.out.println("2" + response.errorBody().toString());

                        e.printStackTrace();
                    }

                    return;
                }

                // Set the values from back
                ProfileInformation Response = response.body();

                JoinHubViewModel.setRoomId(RoomId);
                SearchPerson person = new SearchPerson(Username, Response.getImageLink(), Response.getFirstName(), Response.getLastName());
                JoinHubViewModel.setUser(person);
//                Join();

            }

            @Override
            public void onFailure(Call<ProfileInformation> call, Throwable t) {
                Toast.makeText(Room.this, "Hi ali failed" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

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

    private void Connect() {
//        Platform.loadPlatformComponent(new AndroidPlatformComponent());
//        Credentials credentials = new Credentials() {
//            @Override
//            public void prepareRequest(microsoft.aspnet.signalr.client.http.Request request) {
//                request.addHeader("Authorization", Token); //get username
//            }
//        };
////
//        hubConnection = new HubConnection(Constants.serverUrl);
//        hubConnection.setCredentials(credentials);
//
//
//        hubProxy = hubConnection.createHubProxy("ChatRoomHub"); // web api  necessary method name
//        SignalRFuture<Void> signalRFuture = hubConnection.start();
//        try {
//            signalRFuture.get();
//        } catch (InterruptedException | ExecutionException e) {
//            Log.e("SimpleSignalR", e.toString());
//            return;
//        }

        hubConnection = HubConnectionBuilder.create(Constants.serverUrl).withAccessTokenProvider(Single.defer(() -> {
            return Single.just(Token);
        })).build();

        hubConnection.start();
    }


    public void Leave() {
        hubProxy.invoke("LeaveRoom", JoinHubViewModel);
    }

    public void Join(){
        hubProxy.invoke("JoinRoom", JoinHubViewModel);
    }

}