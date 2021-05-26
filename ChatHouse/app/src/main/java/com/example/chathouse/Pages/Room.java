package com.example.chathouse.Pages;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.SearchPerson;
import com.example.chathouse.ViewModels.Chat.LoadAllMessagesViewModel;
import com.example.chathouse.ViewModels.Chat.ReceiveRoomNotification;
import com.example.chathouse.ViewModels.Room.ChatBoxModel;
import com.example.chathouse.ViewModels.Room.GetRoomViewModel;
import com.example.chathouse.ViewModels.Chat.JoinRoomViewModel;
import com.example.chathouse.ViewModels.Chat.MessageViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class Room extends FragmentActivity {
    private TextView RoomName;
    private ImageButton Leave;
    private GetRoomViewModel RoomInfo;
    private String Creator;
    private String Name;
    HubConnection hubConnection; //Do the signalR definitions
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
    public ChatBoxAdaptor ChatAdaptor;
    public ArrayList<ChatBoxModel> Chats = new ArrayList<ChatBoxModel>();
    public ListView chatBoxListView;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Bundle bundle = getIntent().getExtras();

        RoomName = (TextView) findViewById(R.id.RoomName);
        Send = (TextView) findViewById(R.id.SendButton);
        MessageText = (EditText) findViewById(R.id.Message);
        chatBoxListView = (ListView) findViewById(R.id.chatBox);
        Leave = (ImageButton) findViewById(R.id.LeaveRoom);

        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        Token = settings.getString("Token", "n/a");
        String Username = settings.getString("Username", "n/a");

        RoomId = bundle.getInt("RoomId");

        ChatBoxModel x = new ChatBoxModel();
        x.setFirstName("ali");
        x.setLastName("farahat");
        x.setMessage("meeeeeeeessfsdjaflkasdjflkjasdlfk\nfjsldkjflkajslfkdsadf\nfd\nfdff");
        x.setTime(new Date());
        x.setMode(-1);
        Chats.add(x);

        ChatBoxModel y = new ChatBoxModel();
        y.setFirstName("ali");
        y.setLastName("farahat");
        y.setMessage("meeeeeeeessfsdjaflkasdjflkjasdlfk\nfjsldkjflkajslfkdsadf\nfd\nfdff");
        y.setTime(new Date());
        y.setMode(1);
        Chats.add(y);

        ChatAdaptor = new ChatBoxAdaptor(Room.this, Chats);
        chatBoxListView.setAdapter(ChatAdaptor);
        ChatAdaptor.notifyDataSetChanged();


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
        Call<Void> LeaveRoom = APIS.LeaveRoom(RoomId);
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


        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MessageText.getText().toString().equals("")) {
                    Message.setMessage(MessageText.getText().toString().trim());
                    Message.setMe(true);
                    Message.setRoomId(RoomId);

                    me = new SearchPerson(Response.getUsername(), Response.getImageLink(), Response.getFirstName(), Response.getLastName());

                    Message.setUserModel(me);
                    MessageText.setText("");
                    SendMessage(Message);
                }

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
            ChatBoxModel x = new ChatBoxModel();
            x.setFirstName(messageModel.getUserModel().getFirstName());
            x.setLastName(messageModel.getUserModel().getLastName());
            x.setMessage(messageModel.getMessage().toString());
            x.setImageLink(messageModel.getUserModel().getImageLink());
            x.setUserName(messageModel.getUserModel().getUsername());
            x.setTime(new Date());
            if (messageModel.isMe() == true)
                x.setMode(1);
            else if (messageModel.isMe() == false)
                x.setMode(-1);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Chats.add(x);
                    ChatAdaptor.notifyDataSetChanged();

                }
            });

            String text = "";
            int RoomId = messageModel.getRoomId();
            String message = messageModel.getMessage().toString();
            String senderLastName = messageModel.getUserModel().getLastName();
            String senderUsername = messageModel.getUserModel().getUsername();
            String senderFirstName = messageModel.getUserModel().getFirstName();
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
            ChatBoxModel x = new ChatBoxModel();
            x.setUserName(ReceiveRoomNotification.getUserModel().getUsername());

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
                    Chats.add(x);
                    ChatAdaptor.notifyDataSetChanged();
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
                   chat.setUserName(x.getSender().getUsername());
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

                        Chats.add(chat);
                        ChatAdaptor.notifyDataSetChanged();

                    }
                });
            }
        }, (Class<List<LoadAllMessagesViewModel>>) (Object) List.class);
    }
}


class ChatBoxAdaptor extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<ChatBoxModel> ChatsList = null;

    public ChatBoxAdaptor(Context context, List<ChatBoxModel> chatsList) {
        mContext = context;
        this.ChatsList = chatsList;
        inflater = LayoutInflater.from(mContext);

    }

    public class ViewHolder {
        TextView message;
        TextView name;
        TextView time;
        ImageView Image;

    }

    @Override
    public int getCount() {
        return ChatsList.size();
    }

    @Override
    public ChatBoxModel getItem(int position) {
        return ChatsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        holder = new ViewHolder();
        ChatBoxModel chat = ChatsList.get(position);
        if (ChatsList.get(position).getMode() == -1) {  //left
            view = inflater.inflate(R.layout.chat_left, null);

            holder.message = (TextView) view.findViewById(R.id.chat_message_left);
            holder.name = (TextView) view.findViewById(R.id.chat_name_left);
            holder.time = (TextView) view.findViewById(R.id.chat_time_left);
            holder.Image = (ImageView) view.findViewById(R.id.chat_image_left);
            view.setTag(holder);

            holder.message.setText(chat.getMessage());
            holder.name.setText(chat.getFirstName() + " " + chat.getLastName());

            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");
            holder.time.setText(formatter.format(chat.getTime()));

            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.default_user_profile)
                    .centerCrop();
            Glide.with(mContext).load(chat.getImageLink())
                    .apply(options).transform(new CircleCrop()).into(holder.Image);

            holder.Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfilePage.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Username", chat.getUserName());
                    intent.putExtras(bundle1);
                    mContext.startActivity(intent);
                }
            });



        } else if (ChatsList.get(position).getMode() == 1) {
            view = inflater.inflate(R.layout.chat_right, null);

            holder.message = (TextView) view.findViewById(R.id.chat_message_right);
            holder.time = (TextView) view.findViewById(R.id.chat_time_right);
            view.setTag(holder);

            holder.message.setText(chat.getMessage());

            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");
            holder.time.setText(formatter.format(chat.getTime()));


        } else if (ChatsList.get(position).getMode() == 0) {
            view = inflater.inflate(R.layout.chat_middle, null);

            holder.message = (TextView) view.findViewById(R.id.chat_middle);
            view.setTag(holder);

            holder.message.setText(chat.getMessage());

        }


        return view;
    }
}

