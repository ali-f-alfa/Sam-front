package com.example.chathouse.Pages;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.MessageViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import okhttp3.HttpUrl;

import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chat extends Fragment {

    HubConnection hubConnection; //Do the signalR definitions
    HubProxy hubProxy;
    Handler mHandler=new Handler(); //listener
    TextView Send;
    EditText Message;
    MessageViewModel MessageModel = new MessageViewModel()  ;
    String Token;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public Chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chat.
     */
    // TODO: Rename and change types and number of parameters
    public static Chat newInstance(String param1, String param2) {
        Chat fragment = new Chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Main function we're going to w ork in, we assume the user has been joined in the hub after being directed to the room
        // Here we just want to receive the messages and show them to the user, and also leave
        // Join. Leave. Receive, Load
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences settings = this.getActivity().getSharedPreferences("Storage", MODE_PRIVATE);
        String Username = settings.getString("Username", "n/a");
        Token = settings.getString("Username", "n/a");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Send = view.findViewById(R.id.SendButton);
        Message = view.findViewById(R.id.Message);

        Connect();

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We should send the message
                SendMessage(Username, MessageModel);
            }
        });

        return view;
    }

    public void SendMessage(String username, Object message){

    }

    private void Connect() {
        try{
            Platform.loadPlatformComponent(new AndroidPlatformComponent());
            Credentials credentials = new Credentials() {
                @Override
                public void prepareRequest(microsoft.aspnet.signalr.client.http.Request request) {
                    request.addHeader("Token", Token); //get username
                }
            };

            hubConnection = new HubConnection(Constants.serverUrl);
            hubConnection.setCredentials(credentials);

            hubProxy = hubConnection.createHubProxy("ChatRoomHub"); // web api  necessary method name
//        ClientTransport clientTransport = new ServerSentEventsTransport((hubConnection.getLogger()));
//        SignalRFuture<Void> signalRFuture = hubConnection.start(clientTransport);
//        try {
//            signalRFuture.get();
//        } catch (InterruptedException | ExecutionException e) {
//            Log.e("SimpleSignalR", e.toString());
//            return;
//        }
            hubConnection.start().get();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

}