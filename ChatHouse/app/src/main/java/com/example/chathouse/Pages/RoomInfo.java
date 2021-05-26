package com.example.chathouse.Pages;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.SearchPerson;
import com.example.chathouse.ViewModels.Chat.JoinRoomViewModel;
import com.example.chathouse.ViewModels.Chat.LoadAllMessagesViewModel;
import com.example.chathouse.ViewModels.Chat.MessageViewModel;
import com.example.chathouse.ViewModels.Chat.ReceiveRoomNotification;
import com.example.chathouse.ViewModels.Room.ChatBoxModel;
import com.example.chathouse.ViewModels.Room.GetRoomViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;
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

public class RoomInfo extends AppCompatActivity {

    private GetRoomViewModel Roominfo;
    private TextView Name;
    private TextView Description;
    private TextView Interest;
    private Button Delete;
    private ListViewAdapter MembersAdapter;
    private ArrayList<SearchPerson> MembersList = new ArrayList<SearchPerson>();
    private ListView MembersListView;
    private Boolean RightToRemove = false;
    private String RmUser;
    private ChatHouseAPI APIS;
    private String Creator;
    HubConnection hubConnection; //Do the signalR definitions
    JoinRoomViewModel JoinHub = new JoinRoomViewModel();
    String Token;
    ProfileInformation Response;
    Gson gson;
//    public ChatBoxAdaptor ChatAdaptor;
//    public ArrayList<ChatBoxModel> Chats = new ArrayList<ChatBoxModel>();
//    public ListView chatBoxListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        Name = (TextView) findViewById(R.id.RoomNameInfoPage);
        Description = (TextView) findViewById(R.id.DescriptionRoom);
        Interest = (TextView) findViewById(R.id.RoomInterest);
        Delete = (Button) findViewById(R.id.DeleteRoom);
        MembersListView = (ListView) findViewById(R.id.RoomMembersListView);
        Bundle bundle = getIntent().getExtras();
//        chatBoxListView = (ListView) findViewById(R.id.chatBox);
//        ChatAdaptor = new ChatBoxAdaptor(RoomInfo.this, Chats);
//        chatBoxListView.setAdapter(ChatAdaptor);

        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        Token = settings.getString("Token", "n/a");
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

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUrl.get(Constants.baseURL))
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory((GsonConverterFactory.create(gson)))
                .build();


        APIS = retrofit.create(ChatHouseAPI.class);

        Call<GetRoomViewModel> GetRoom = APIS.GetRoom(RoomId);
        Call<Void> DeleteRoom = APIS.DeleteRoom(RoomId);
        Call<Void> LeaveRoom = APIS.LeaveRoom(RoomId);

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

            }

            @Override
            public void onFailure(Call<ProfileInformation> call, Throwable t) {
                Toast.makeText(RoomInfo.this, "Hi ali failed" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


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
                Roominfo = response.body();
                SetRoomInfo(Roominfo);
                Creator = Roominfo.getCreator().getUsername();
                if (Creator.equals(Username)) {
                    RightToRemove = true;
                } else {
                    Delete.setText("Leave room");
                }
            }

            @Override
            public void onFailure(Call<GetRoomViewModel> call, Throwable t) {
                Toast.makeText(RoomInfo.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Username.equals(Creator)) {
                    new AlertDialog.Builder(RoomInfo.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Deleting room").setMessage("Are you sure you want to delete this room?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DeleteRoom.enqueue(new Callback<Void>() {
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
                                            Intent intent = new Intent(RoomInfo.this, HomePage.class);

                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(RoomInfo.this, "Request failed", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    finish();
                                    Toast.makeText(RoomInfo.this, "Room deleted", Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("No", null).show();
                } else {
                    LeaveRoom.enqueue(new Callback<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.R)
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
                            Intent intent = new Intent(RoomInfo.this, AcitivityPage.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(RoomInfo.this, "Request failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

        MembersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchPerson p = (SearchPerson) MembersListView.getAdapter().getItem(position);
                if (!RightToRemove) {
                    Intent intent = new Intent(RoomInfo.this, ProfilePage.class);
                    Bundle bundle1 = new Bundle();
                    String username = p.getUsername();
                    bundle1.putString("Username", username);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                } else {
                    showPopup(view, p, RoomId);
                }
            }
        });

    }

    public void showPopup(View v, SearchPerson position, int roomId) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String Username = position.getUsername();

                switch (item.getItemId()) {
                    case R.id.remove:
                        Call<GetRoomViewModel> removeUser = APIS.RemoveUser(roomId, Username);
                        new AlertDialog.Builder(RoomInfo.this).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Removing member").setMessage("Are you sure you want to remove this member?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeUser.enqueue(new Callback<GetRoomViewModel>() {
                                            @Override
                                            public void onResponse(Call<GetRoomViewModel> call, Response<GetRoomViewModel> response) {
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
                                                MembersList.remove(position);
                                                MembersAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onFailure(Call<GetRoomViewModel> call, Throwable t) {
                                                Toast.makeText(RoomInfo.this, "Request failed", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        finish();
                                        Toast.makeText(RoomInfo.this, "Member removed", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("No", null).show();


                        break;
                    case R.id.showPro:
                        Intent intent = new Intent(RoomInfo.this, ProfilePage.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Username", Username);
                        intent.putExtras(bundle1);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }

    private void SetRoomInfo(GetRoomViewModel roominfo) {
        Name.setText(roominfo.getName());
        Description.setText(roominfo.getDescription());
        Interest.setText(InterestName(roominfo.getInterests()));

        for (SearchPerson person : roominfo.getMembers()) {
            SearchPerson Person = new SearchPerson(person.getUsername(), person.getImageLink(), person.getFirstName(), person.getLastName());
            MembersList.add(Person);
        }

        MembersAdapter = new ListViewAdapter(RoomInfo.this, MembersList);
        MembersListView.setAdapter(MembersAdapter);
        MembersAdapter.notifyDataSetChanged();
    }

    private String InterestName(ArrayList<ArrayList<Integer>> interests) {
        String temp = "";
        for (int i = 0; i < 14; i++) {
            ArrayList<Integer> tt = interests.get(i);
            if (tt.isEmpty()) {
                continue;
            }
            int ttt = (int) (Math.log(tt.get(0)) / Math.log(2));

            switch (i) {
                case 0:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Wellness.getArrayString().get(ttt);
                    break;
                case 1:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Identity.getArrayString().get(ttt);
                    break;
                case 2:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Places.getArrayString().get(ttt);
                    break;
                case 3:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.WorldAffairs.getArrayString().get(ttt);
                    break;
                case 4:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Tech.getArrayString().get(ttt);
                    break;
                case 5:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.HangingOut.getArrayString().get(ttt);
                    break;
                case 6:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.KnowLedge.getArrayString().get(ttt);
                    break;
                case 7:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Hustle.getArrayString().get(ttt);
                    break;
                case 8:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Sports.getArrayString().get(ttt);
                    break;
                case 9:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Arts.getArrayString().get(ttt);
                    break;
                case 10:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Life.getArrayString().get(ttt);
                    break;
                case 11:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Languages.getArrayString().get(ttt);
                    break;
                case 12:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Entertainment.getArrayString().get(ttt);
                    break;
                case 13:
                    temp = com.example.chathouse.ViewModels.Acount.Interests.Faith.getArrayString().get(ttt);
                    break;
            }

        }
        return temp;
    }

    private void Connect() {

        hubConnection = HubConnectionBuilder.create(Constants.serverUrl).withAccessTokenProvider(Single.defer(() -> {
            return Single.just(Token);
        })).build();

        hubConnection.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void Leave() {
        Connect();
//        DefineMethods();
        try {
            hubConnection.invoke("LeaveRoom", JoinHub);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void DefineMethods() {

        hubConnection.on("ReceiveRoomNotification", (ReceiveRoomNotification) ->
        {
            ChatBoxModel x = new ChatBoxModel();
            x.setMode(0);

            if (ReceiveRoomNotification.getNotification() == 0) {
                if (ReceiveRoomNotification.getMe()) {
                    x.setMessage("You joined this room");

                } else {
                    x.setMessage(ReceiveRoomNotification.getUserModel().getUsername() + " join this room");
                }
            } else {
                if (ReceiveRoomNotification.getMe()) {
                    x.setMessage("You left this room");
                } else {
                    x.setMessage(ReceiveRoomNotification.getUserModel().getUsername() + " left this room");
                }
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                    Chats.add(x);
//                    ChatAdaptor.notifyDataSetChanged();
                }
            });
        }, ReceiveRoomNotification.class);

        hubConnection.on("ReceiveRoomAllMessages", (messageModel) ->
        {
            for (Object X : messageModel) {

                String s2 = gson.toJson(X);
                LoadAllMessagesViewModel x = gson.fromJson(s2, LoadAllMessagesViewModel.class);
                ChatBoxModel chat = new ChatBoxModel();
                int contentType = x.contetntType;
                if(contentType == 0){
                    chat.setFirstName(x.getSender().getFirstName());
                    chat.setLastName(x.getSender().getLastName());
                    chat.setImageLink(x.getSender().getImageLink());
                    chat.setTime(x.sentDate);
                    if (x.getMe() == true)
                        chat.setMode(1);
                    else if (x.getMe() == false)
                        chat.setMode(-1);
                    chat.setMessage(x.getContent());
                }
                else if(contentType == 2){
                    chat.setMode(0);
                    if (x.getMe()) {
                        chat.setMessage("You joined this room");

                    } else {
                        chat.setMessage(x.getSender().getUsername() + " join this room");
                    }
                }
                else if(contentType == 3){
                    chat.setMode(0);
                    if (x.getMe()) {
                        chat.setMessage("You left this room");

                    } else {
                        chat.setMessage(x.getSender().getUsername() + " left this room");
                    }
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

//                        Chats.add(chat);
//                        ChatAdaptor.notifyDataSetChanged();

                    }
                });
            }
        }, (Class<List<LoadAllMessagesViewModel>>) (Object) List.class);
    }

}