package com.example.chathouse.Pages;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.SearchPerson;
import com.example.chathouse.ViewModels.Chat.LoadAllMessagesViewModel;
import com.example.chathouse.ViewModels.Chat.ReceiveRoomNotification;
import com.example.chathouse.ViewModels.Room.GetRoomViewModel;
import com.example.chathouse.ViewModels.Chat.JoinRoomViewModel;
import com.example.chathouse.ViewModels.Chat.MessageViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class Room extends FragmentActivity {
    private Button Leave;
    private TextView RoomName;
    private GetRoomViewModel RoomInfo;
    private String Creator;
    private String Name;
    HubConnection hubConnection; //Do the signalR definitions
    HubProxy hubProxy;
    Handler mHandler = new Handler(); //listener
    JoinRoomViewModel JoinHub = new JoinRoomViewModel();
    int RoomId;
    String Token;
    MessageViewModel Message = new MessageViewModel();
    private TextView Send;
    private EditText MessageText;
    SearchPerson me;
    ArrayList<LoadAllMessagesViewModel> LoadMessages = new ArrayList<>();
    ProfileInformation Response;
    Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Bundle bundle = getIntent().getExtras();

        Leave = (Button) findViewById(R.id.LeaveRoom);
        RoomName = (TextView) findViewById(R.id.RoomName);
        Send = (TextView) findViewById(R.id.SendButton);
        MessageText = (EditText) findViewById(R.id.Message);


        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        Token = settings.getString("Token", "n/a");
        String Username = settings.getString("Username", "n/a");

        RoomId = bundle.getInt("RoomId");


        // Hub Join
        Connect();
        DefineMethods();

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", Token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        gson = new GsonBuilder()
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
                Response = response.body();

                JoinHub.setRoomId(RoomId);
                SearchPerson person = new SearchPerson(Username, Response.getImageLink(), Response.getFirstName(), Response.getLastName());
                JoinHub.setUser(person);
                Join();
                LoadAllMessages(Username, RoomId);

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
                if (!response.isSuccessful()) {
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

                if (Creator.equals(Username)) {
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
                        if (!response.isSuccessful()) {
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
                        Leave();
                        Intent intent = new Intent(Room.this, AcitivityPage.class);

                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(Room.this, "Request failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MessageText.getText().toString().equals("")) {
                    Message.setMessage(MessageText.getText().toString());
                    Message.setMe(true);
                    Message.setRoomId(RoomId);

                    me = new SearchPerson(Response.getUsername(), Response.getImageLink(), Response.getFirstName(), Response.getLastName());

                    Message.setUserModel(me);
                    MessageText.setText("");
                    SendMessage(Message);
                }

            }
        });
    }

    private void Connect() {

        hubConnection = HubConnectionBuilder.create(Constants.serverUrl).withAccessTokenProvider(Single.defer(() -> {
            return Single.just(Token);
        })).build();

        hubConnection.start();

    }


    public void Leave() {
        try {
            hubConnection.invoke("LeaveRoom", JoinHub);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void Join() {
        try {
            hubConnection.invoke("JoinRoom", JoinHub);


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void SendMessage(MessageViewModel message) {
        try {
            hubConnection.invoke("SendMessageToRoom", message);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void LoadAllMessages(String username, int roomId) {
        try {
            hubConnection.invoke("LoadRoomMessages", roomId, username);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void DefineMethods() {
        hubConnection.on("ReceiveRoomMessage", (messageModel) ->
        {
            String text = "";
            int RoomId = messageModel.getRoomId();
//            int messageType = messageModel.getMessageType();
            String message = messageModel.getMessage().toString();
            String senderLastName = messageModel.getUserModel().getLastName();
            String senderUsername = messageModel.getUserModel().getUsername();
            String senderFirstName = messageModel.getUserModel().getFirstName();
            String senderImageLink = messageModel.getUserModel().getImageLink();
            Boolean IsMe = messageModel.isMe();
            if (IsMe) {
                text += "Your Message To  Room number #" + RoomId + ":";
            } else {
                text += "New Message From " + senderFirstName + " " + senderLastName + " To  Room number #" + RoomId + ":";
            }

//            Toast.makeText(Room.this, text + message, Toast.LENGTH_LONG).show();
            System.out.println(text + message);
        }, MessageViewModel.class);

        hubConnection.on("ReceiveRoomNotification", (ReceiveRoomNotification) ->
        {
            if (ReceiveRoomNotification.getNotification() == 0) {
                if (ReceiveRoomNotification.getMe()) {
//                   Toast.makeText(Room.this, "You joined room number " + ReceiveRoomNotification.getRoomId(), Toast.LENGTH_LONG).show();
                    System.out.println("You joined room number " + ReceiveRoomNotification.getRoomId());


                } else {
//               Toast.makeText(Room.this, ReceiveRoomNotification.getUserModel().getUsername() + " Joined room number " + ReceiveRoomNotification.getRoomId(), Toast.LENGTH_LONG).show();
                    System.out.println(ReceiveRoomNotification.getUserModel().getUsername() + " Joined room number " + ReceiveRoomNotification.getRoomId());
                }
            } else {
                if (ReceiveRoomNotification.getMe()) {
//                        Toast.makeText(Room.this, "You left room number "  + ReceiveRoomNotification.getRoomId(), Toast.LENGTH_LONG).show();
                    System.out.println("You left room number " + ReceiveRoomNotification.getRoomId());
                } else {
//                        Toast.makeText(Room.this, ReceiveRoomNotification.getUserModel().getUsername() + " Left room number " + ReceiveRoomNotification.getRoomId(), Toast.LENGTH_LONG).show();
                    System.out.println(ReceiveRoomNotification.getUserModel().getUsername() + " Left room number " + ReceiveRoomNotification.getRoomId());
                }
            }
        }, ReceiveRoomNotification.class);

        hubConnection.on("ReceiveRoomAllMessages", (messageModel) ->
        {
            for (Object X: messageModel)
            {

                String s2 = gson.toJson(X);
                LoadAllMessagesViewModel x = gson.fromJson(s2, LoadAllMessagesViewModel .class);

                String sender = "";
                if (x.isMe) sender = "You";
                else sender = x.sender.getUsername();
                if (x.contetntType == 0)
                {
                    System.out.println(sender + " : " + x.content + "\t" + x.sentDate.toString());
                }
                else if (x.contetntType == 2)
                {
                    System.out.println(sender + " Joined room number " + x.roomId);
                }
                else if (x.contetntType == 3)
                {
                    System.out.println(sender + " Left room number " + x.roomId);
                }
            }
            System.out.println("Notification");
        }, (Class<List<LoadAllMessagesViewModel>>)(Object)List.class);
    }


}