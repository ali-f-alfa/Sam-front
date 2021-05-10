package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.chathouse.ViewModels.Acount.SearchPerson;
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

public class RoomInfo extends AppCompatActivity{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        Name = (TextView)findViewById(R.id.RoomNameInfoPage);
        Description = (TextView)findViewById(R.id.DescriptionRoom);
        Interest = (TextView)findViewById(R.id.RoomInterest);
        Delete = (Button)findViewById(R.id.DeleteRoom);
        MembersListView = (ListView)findViewById(R.id.RoomMembersListView);
        Delete.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();


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
                Roominfo = response.body();
                SetRoomInfo(Roominfo);
                Creator = Roominfo.getCreator().getUsername();
                if(Creator.equals(Username)){
                    Delete.setVisibility(View.VISIBLE);
                    RightToRemove = true;
                }
                System.out.println("Works");

            }

            @Override
            public void onFailure(Call<GetRoomViewModel> call, Throwable t) {
                Toast.makeText(RoomInfo.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteRoom.enqueue(new Callback<Void>() {
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
                        Intent intent = new Intent(RoomInfo.this, HomePage.class);

                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RoomInfo.this, "Request failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        MembersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SearchPerson p = (SearchPerson) MembersListView.getAdapter().getItem(position);
                if(!RightToRemove){
                    Intent intent = new Intent(RoomInfo.this, ProfilePage.class);
                    Bundle bundle1 = new Bundle();
                    String username = p.getUsername();
                    bundle1.putString("Username", username);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
                else{
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

                switch (item.getItemId()){
                    case R.id.remove:
                        Call<GetRoomViewModel> removeUser = APIS.RemoveUser(roomId, Username);
                        removeUser.enqueue(new Callback<GetRoomViewModel>() {
                            @Override
                            public void onResponse(Call<GetRoomViewModel> call, Response<GetRoomViewModel> response) {
                                if(!response.isSuccessful()){
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
        popup.show();
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
            if(tt.isEmpty()){
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
}