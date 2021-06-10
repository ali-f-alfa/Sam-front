package com.example.chathouse.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.ListView;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.ProgressBar;
import android.widget.HorizontalScrollView;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.RoomModel;
import com.example.chathouse.ViewModels.Acount.SearchPerson;
import com.example.chathouse.ViewModels.Search.InputRoomSearchViewModel;
import com.example.chathouse.ViewModels.Search.InputSearchViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;

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

public class Search extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // Declare Variables
    ListView list;
    ListView RoomList;
    ListViewAdapter adapter;
    RoomListViewAdapter RoomAdapter;
    SearchView editsearch;
    ArrayList<SearchPerson> SearchedPersons = new ArrayList<SearchPerson>();
    ArrayList<SearchRoom> SearchedRooms = new ArrayList<SearchRoom>();
    TextView SearchError;
    TextView SearchTitle;
    int i = 3;
    int r = 3;
    SharedPreferences settings;
    String Token;
    String Username;
    ChatHouseAPI SearchAPI;
    int mode;
    int selected_category;
    int selected_item;
    boolean endOfList;
    boolean RoomEndOfList;
    GridLayout category_grid;
    ScrollView category_scroll;
    GridLayout item_grid;
    HorizontalScrollView item_scroll;
    BottomNavigationView menu;
    ProgressBar loading;
    ToggleSwitch toggleSwitch;
    int isRoom;
    ProfileInformation Response;
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings  = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String themeName = settings.getString("ThemeName", "DarkTheme");
        if (themeName.equalsIgnoreCase("DarkTheme")) {
            setTheme(R.style.DarkTheme_ChatHouse);
        } else if (themeName.equalsIgnoreCase("Theme")) {
            setTheme(R.style.Theme_ChatHouse);
        }
        setContentView(R.layout.activity_search);

        mode = 0; // 0 => suggest  ,  1 => category  , 2 => item
        selected_category = 0;
        selected_item = 0;
        endOfList = false;
        RoomEndOfList = false;
        SearchError = findViewById(R.id.SearchError);
        SearchTitle = findViewById(R.id.SearchTitle);
        list = (ListView) findViewById(R.id.SearchedPersonListView);
        RoomList = (ListView) findViewById(R.id.SearchedRoomListView);
        editsearch = (SearchView) findViewById(R.id.search);
        settings = getSharedPreferences("Storage", MODE_PRIVATE);
        Token = settings.getString("Token", "n/a");
        Username = settings.getString("Username", "n/a");
        category_grid = (GridLayout) findViewById(R.id.category_grid);
        category_scroll = (ScrollView) findViewById(R.id.Category);
        item_grid = (GridLayout) findViewById(R.id.item_grid);
        item_scroll = (HorizontalScrollView) findViewById(R.id.Items);
        menu = (BottomNavigationView) findViewById(R.id.Search_menu);
        loading = (ProgressBar) findViewById(R.id.search_progressbar);
        toggleSwitch = (ToggleSwitch) findViewById(R.id.search_switch);
        isRoom = 0;
        layout = (RelativeLayout)findViewById(R.id.searchback);
        if (themeName.equalsIgnoreCase("DarkTheme")) {
            layout.setBackgroundResource(R.drawable.b22d);
        } else if (themeName.equalsIgnoreCase("Theme")) {
            layout.setBackgroundResource(R.drawable.b22);
        }

        toggleSwitch.setCheckedPosition(0);

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
        SearchAPI = retrofit.create(ChatHouseAPI.class);

        menu.setOnNavigationItemSelectedListener(navListener);

        int childCount = category_grid.getChildCount();
        for (int i = 0; i < childCount; i++) {
            int n = i;
            CardView container = (CardView) category_grid.getChildAt(i);
            container.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    selected_category = n;
                    mode = 1;

                    SearchedPersons.clear();
                    SearchedRooms.clear();
                    SearchError.setVisibility(View.INVISIBLE);
                    TextView t = (TextView) container.getChildAt(0);
                    SearchTitle.setText("Search in " + t.getText());


                    Call<List<InputSearchViewModel>> Req = SearchAPI.Category(editsearch.getQuery().toString(), selected_category, 10, 1);
                    Call<List<InputRoomSearchViewModel>> RoomReq = SearchAPI.RoomCategory(editsearch.getQuery().toString(), selected_category, 10, 1);
                    loading.setVisibility(View.VISIBLE);
                    Req.enqueue(new Callback<List<InputSearchViewModel>>() {
                        @Override
                        public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(Search.this, "unsuccessful", Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                            }
                            for (InputSearchViewModel person : response.body()) {
                                SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                                SearchedPersons.add(Person);
                            }

                            adapter = new ListViewAdapter(Search.this, SearchedPersons);
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if (SearchedPersons.size() == 0) {
                                if(isRoom == 0)
                                    SearchError.setVisibility(View.VISIBLE);
                            }
                            loading.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                            Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);

                        }
                    });
                    RoomReq.enqueue(new Callback<List<InputRoomSearchViewModel>>() {
                        @Override
                        public void onResponse(Call<List<InputRoomSearchViewModel>> call, Response<List<InputRoomSearchViewModel>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(Search.this, "unsuccessful", Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                            }
                            for (InputRoomSearchViewModel room : response.body()) {
                                SearchRoom R = new SearchRoom(room.getId(), room.getName(), room.getDescription(), room.getStartDate(), room.getInterests(), room.getMembersCount());
                                SearchedRooms.add(R);
                            }

                            RoomAdapter = new RoomListViewAdapter(Search.this, SearchedRooms);
                            RoomList.setAdapter(RoomAdapter);
                            RoomAdapter.notifyDataSetChanged();

                            if (SearchedRooms.size() == 0) {
                                if(isRoom == 1)
                                    SearchError.setVisibility(View.VISIBLE);
                            }
                            loading.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(Call<List<InputRoomSearchViewModel>> call, Throwable t) {
                            Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);

                        }
                    });

                    category_scroll.setVisibility(View.INVISIBLE);
                    CreateItems(n, item_grid);
                }
            });
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Search.this, com.example.chathouse.Pages.ProfilePage.class);
                Bundle bundle = new Bundle();

                SearchPerson p = (SearchPerson) list.getAdapter().getItem(position);
                String Username = p.getUsername();
                bundle.putString("Username", Username);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        RoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SearchRoom R = (SearchRoom) RoomList.getAdapter().getItem(position);
                int RoomId = R.getId();
                Date objDate = new Date();
                Date date = R.getStartDate();
                String Name = R.getName();
                Call<ProfileInformation> GetProfile = SearchAPI.GetProfile(Username);

                GetProfile.enqueue(new Callback<ProfileInformation>() {
                    @Override
                    public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                        if (!response.isSuccessful()) {
                            try {
                                loading.setVisibility(View.INVISIBLE);
                                System.out.println("1" + response.errorBody().string());
                                System.out.println("1" + response.code());
                            } catch (IOException e) {
                                loading.setVisibility(View.INVISIBLE);
                                System.out.println("2" + response.errorBody().toString());

                                e.printStackTrace();
                            }

                            return;
                        }

                        // Set the values from back
                        Response = response.body();
                        System.out.println(Response.getInRooms());
                        ArrayList<RoomModel> inRooms = Response.getInRooms();
                        ArrayList<RoomModel> roomsCreated = Response.getCreatedRooms();
                        Boolean included = false;
                        Boolean isCreator = false;
                        for(RoomModel x: inRooms){
                            if(x.getRoomId() == RoomId){
                                included = true;
                                isCreator = false;
                            }
                        }
                        for(RoomModel x: roomsCreated){
                            if(x.getRoomId() == RoomId){
                                included = true;
                                isCreator = false;
                            }
                        }
                        if(included){
                            if(date == null || date.compareTo(objDate) < 0){
                                Intent intent = new Intent(Search.this, com.example.chathouse.Pages.Room.class);
                                Bundle bundle = new Bundle();

                                bundle.putInt("RoomId", RoomId);
//                                bundle.putString("Name", Name);
//                            bundle.putString("Creator", p.getCreator().getUsername());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                            else{
                                if (isCreator) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                                    alert.setTitle("your Room");

                                    alert.setMessage("you can edit your room in activity page");
//


                                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //Your action here
                                        }
                                    });
                                    alert.show();
                                }
                                else{
                                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

                                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm  dd MMMM yyyy");
                                    alert.setMessage("Room start time is: \n" + formatter.format(date));


                                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //Your action here
                                        }
                                    });
                                    alert.show();
                                }
                            }
                        }
                        else{
                            Call<ProfileInformation> JoinRoom = SearchAPI.JoinRoom(RoomId);
                            JoinRoom.enqueue(new Callback<ProfileInformation>() {
                                @Override
                                public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
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
                                    ProfileInformation p = response.body();
                                    if(date == null || date.compareTo(objDate) < 0){
                                        Toast.makeText(Search.this, "successfully joined the room", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Search.this, com.example.chathouse.Pages.Room.class);
                                        Bundle bundle = new Bundle();

                                        bundle.putInt("RoomId", RoomId);
                                        bundle.putString("Name", Name);
//                            bundle.putString("Creator", p.getCreator().getUsername());
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    }
                                    else{
                                        // Show them count down
                                        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                                        alert.setTitle("successfully joined");

                                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm  dd MMMM yyyy");
                                        alert.setMessage("Room start time is: \n" + formatter.format(date));


                                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                //Your action here
                                            }
                                        });
                                        alert.show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<ProfileInformation> call, Throwable t) {
                                    Toast.makeText(Search.this, "Request Failed" + RoomId, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileInformation> call, Throwable t) {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(Search.this, "Request failed" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });




            }
        });

        Call<List<InputSearchViewModel>> Suggest = SearchAPI.Suggest(10, 1);
        Call<List<InputRoomSearchViewModel>> RoomSuggest = SearchAPI.RoomCategory("", null, 10, 1);
        loading.setVisibility(View.VISIBLE);
        Suggest.enqueue(new Callback<List<InputSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "request was not successful ", Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.GONE);
                }
                else {
                    for (InputSearchViewModel person : response.body()) {
                        SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                        SearchedPersons.add(Person);
                    }

                    adapter = new ListViewAdapter(Search.this, SearchedPersons);
                    list.setAdapter(adapter);

                    if (SearchedPersons.size() == 0) {
                        if(isRoom == 0)
                            SearchError.setVisibility(View.VISIBLE);
                    }
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);

            }
        });
        RoomSuggest.enqueue(new Callback<List<InputRoomSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputRoomSearchViewModel>> call, Response<List<InputRoomSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "request was not successful ", Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.GONE);
                }
                else {
                    for (InputRoomSearchViewModel room : response.body()) {
                        SearchRoom R = new SearchRoom(room.getId(), room.getName(), room.getDescription(), room.getStartDate(), room.getInterests(), room.getMembersCount());
                        SearchedRooms.add(R);
                    }

                    RoomAdapter = new RoomListViewAdapter(Search.this, SearchedRooms);
                    RoomList.setAdapter(RoomAdapter);
                    RoomAdapter.notifyDataSetChanged();

                    if (SearchedRooms.size() == 0) {
                        if(isRoom == 1)
                            SearchError.setVisibility(View.VISIBLE);
                    }
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<InputRoomSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Check your networkk", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);

            }
        });

        //pagination
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (!endOfList) {
                    if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 &&
                            list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) { //end of scroll


                        Call<List<InputSearchViewModel>> Req;
                        if (mode == 0) {
                            String xxx = editsearch.getQuery().toString();
                            Req = SearchAPI.Category(editsearch.getQuery().toString(), null, 5, i++);
                        } else if (mode == 1)
                            Req = SearchAPI.Category(editsearch.getQuery().toString(), selected_category, 5, i++);
                        else
                            Req = SearchAPI.Item(editsearch.getQuery().toString(), selected_category, selected_item, 5, i++);

                        loading.setVisibility(View.VISIBLE);
                        Req.enqueue(new Callback<List<InputSearchViewModel>>() {
                            @Override
                            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(Search.this, "request was not successful ", Toast.LENGTH_LONG).show();
                                    loading.setVisibility(View.GONE);
                                }
                                else {

                                    if (response.body().size() == 0)
                                        endOfList = true;
                                    else
                                        endOfList = false;

                                    for (InputSearchViewModel person : response.body()) {
                                        SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                                        SearchedPersons.add(Person);
                                    }

                                    adapter.notifyDataSetChanged();


                                    if (SearchedPersons.size() == 0) {
                                        if(isRoom == 0)
                                            SearchError.setVisibility(View.VISIBLE);
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                                Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        RoomList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (!RoomEndOfList) {
                    if (RoomList.getLastVisiblePosition() == RoomList.getAdapter().getCount() - 1 &&
                            RoomList.getChildAt(RoomList.getChildCount() - 1).getBottom() <= RoomList.getHeight()) { //end of scroll


                        Call<List<InputSearchViewModel>> Req;
                        Call<List<InputRoomSearchViewModel>> RoomReq;
                        if (mode == 0) {
                            Req = SearchAPI.Category(editsearch.getQuery().toString(), null, 5, i++);
                            RoomReq = SearchAPI.RoomCategory(editsearch.getQuery().toString(), null, 5, r++);
                        } else if (mode == 1) {
                            Req = SearchAPI.Category(editsearch.getQuery().toString(), selected_category, 5, i++);
                            RoomReq = SearchAPI.RoomCategory(editsearch.getQuery().toString(), selected_category, 5, r++);
                        }
                        else {
                            Req = SearchAPI.Item(editsearch.getQuery().toString(), selected_category, selected_item, 5, i++);
                            RoomReq = SearchAPI.RoomItem(editsearch.getQuery().toString(), selected_category, selected_item, 5, r++);
                        }
                        loading.setVisibility(View.VISIBLE);
                        Req.enqueue(new Callback<List<InputSearchViewModel>>() {
                            @Override
                            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(Search.this, "request was not successful ", Toast.LENGTH_LONG).show();
                                    loading.setVisibility(View.GONE);
                                }
                                else {

                                    if (response.body().size() == 0)
                                        endOfList = true;
                                    else
                                        endOfList = false;

                                    for (InputSearchViewModel person : response.body()) {
                                        SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                                        SearchedPersons.add(Person);
                                    }

                                    adapter.notifyDataSetChanged();


                                    if (SearchedPersons.size() == 0) {
                                        SearchError.setVisibility(View.VISIBLE);
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                                Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                            }
                        });

                        RoomReq.enqueue(new Callback<List<InputRoomSearchViewModel>>() {
                            @Override
                            public void onResponse(Call<List<InputRoomSearchViewModel>> call, Response<List<InputRoomSearchViewModel>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(Search.this, "request was not successful ", Toast.LENGTH_LONG).show();
                                    loading.setVisibility(View.GONE);
                                }
                                else {

                                    if (response.body().size() == 0)
                                        RoomEndOfList = true;
                                    else
                                        RoomEndOfList = false;

                                    for (InputRoomSearchViewModel room : response.body()) {
                                        SearchRoom R = new SearchRoom(room.getId(), room.getName(), room.getDescription(), room.getStartDate(), room.getInterests(), room.getMembersCount());
                                        SearchedRooms.add(R);
                                    }

                                    RoomAdapter.notifyDataSetChanged();


                                    if (SearchedRooms.size() == 0) {
                                        if(isRoom == 1)
                                            SearchError.setVisibility(View.VISIBLE);
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<InputRoomSearchViewModel>> call, Throwable t) {
                                Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        toggleSwitch.setOnChangeListener(new ToggleSwitch.OnChangeListener(){
            @Override
            public void onToggleSwitchChanged(int position) {
//                Toast.makeText(Search.this, "wtf", Toast.LENGTH_LONG).show();

                isRoom = position;
                if (isRoom == 0) {
                    list.setVisibility(View.VISIBLE);
                    RoomList.setVisibility(View.GONE);

                    if (SearchedPersons.size() == 0)
                        SearchError.setVisibility(View.VISIBLE);
                    else
                        SearchError.setVisibility(View.GONE);

                    if (mode == 0)
                        SearchTitle.setText("Search in all people");

                }
                else if(isRoom == 1){
                    list.setVisibility(View.GONE);
                    RoomList.setVisibility(View.VISIBLE);

                    if (SearchedRooms.size() == 0)
                        SearchError.setVisibility(View.VISIBLE);
                    else
                        SearchError.setVisibility(View.GONE);

                    if(mode == 0)
                        SearchTitle.setText("Search in all Rooms");

                }

            }
        });
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        editsearch.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        SearchError.setVisibility(View.INVISIBLE);

        SearchedPersons.clear();
        adapter.notifyDataSetChanged();
        endOfList = false;
        Call<List<InputSearchViewModel>> Req;
        Call<List<InputRoomSearchViewModel>> RoomReq;
        if (mode == 0) {
            Req = SearchAPI.Category(newText, null, 10, 1);
            RoomReq = SearchAPI.RoomCategory(newText, null, 10, 1);
        }
        else if (mode == 1){
            Req = SearchAPI.Category(editsearch.getQuery().toString(), selected_category, 10, 1);
            RoomReq = SearchAPI.RoomCategory(editsearch.getQuery().toString(), selected_category, 10, 1);
        }
        else {
            Req = SearchAPI.Item(editsearch.getQuery().toString(), selected_category, selected_item, 10, 1);
            RoomReq = SearchAPI.RoomItem(editsearch.getQuery().toString(), selected_category, selected_item, 10, 1);
        }
        loading.setVisibility(View.VISIBLE);
        Req.enqueue(new Callback<List<InputSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "unsuccessful ", Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.GONE);
                }
                else {
                    SearchedPersons.clear();
                    adapter.notifyDataSetChanged();
                    for (InputSearchViewModel person : response.body()) {
                        SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                        SearchedPersons.add(Person);
                    }

                    adapter = new ListViewAdapter(Search.this, SearchedPersons);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    if (SearchedPersons.size() == 0) {
                        if(isRoom == 0)
                            SearchError.setVisibility(View.VISIBLE);
                    }
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);

            }
        });
        RoomReq.enqueue(new Callback<List<InputRoomSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputRoomSearchViewModel>> call, Response<List<InputRoomSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "unsuccessful ", Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.GONE);
                }
                else {
                    SearchedRooms.clear();
                    for (InputRoomSearchViewModel room : response.body()) {
                        SearchRoom R = new SearchRoom(room.getId(), room.getName(), room.getDescription(), room.getStartDate(), room.getInterests(), room.getMembersCount());
                        SearchedRooms.add(R);
                    }

                    RoomAdapter = new RoomListViewAdapter(Search.this, SearchedRooms);
                    RoomList.setAdapter(RoomAdapter);
                    RoomAdapter.notifyDataSetChanged();

                    if (SearchedRooms.size() == 0) {
                        if(isRoom == 1)
                            SearchError.setVisibility(View.VISIBLE);
                    }
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<InputRoomSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);

            }
        });

        return false;
    }


    public void CreateItems(int n, GridLayout layout) {
        ArrayList<String> temp = new ArrayList<>();
        switch (n) {
            case 0:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Wellness.getArrayString();
                break;
            case 1:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Identity.getArrayString();
                break;
            case 2:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Places.getArrayString();
                break;
            case 3:
                temp = com.example.chathouse.ViewModels.Acount.Interests.WorldAffairs.getArrayString();
                break;
            case 4:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Tech.getArrayString();
                break;
            case 5:
                temp = com.example.chathouse.ViewModels.Acount.Interests.HangingOut.getArrayString();
                break;
            case 6:
                temp = com.example.chathouse.ViewModels.Acount.Interests.KnowLedge.getArrayString();
                break;
            case 7:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Hustle.getArrayString();
                break;
            case 8:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Sports.getArrayString();
                break;
            case 9:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Arts.getArrayString();
                break;
            case 10:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Life.getArrayString();
                break;
            case 11:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Languages.getArrayString();
                break;
            case 12:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Entertainment.getArrayString();
                break;
            case 13:
                temp = com.example.chathouse.ViewModels.Acount.Interests.Faith.getArrayString();
                break;
        }

        for (int x = 0; x < temp.size(); x++) {
            int xx = x;
            String t = temp.get(x);
            AddItem(temp.get(x), item_grid).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_item = (int) Math.pow(2, xx);
                    mode = 2;

                    SearchedPersons.clear();
                    SearchedRooms.clear();
                    SearchError.setVisibility(View.INVISIBLE);

                    Call<List<InputSearchViewModel>> Req = SearchAPI.Item(editsearch.getQuery().toString(), selected_category, selected_item, 10, 1);
                    Call<List<InputRoomSearchViewModel>> RoomReq = SearchAPI.RoomItem(editsearch.getQuery().toString(), selected_category, selected_item, 10, 1);
                    loading.setVisibility(View.VISIBLE);
                    Req.enqueue(new Callback<List<InputSearchViewModel>>() {
                        @Override
                        public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(Search.this, "unsuccessful", Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                            }
                            else {
                                for (InputSearchViewModel person : response.body()) {
                                    SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                                    SearchedPersons.add(Person);
                                }

                                adapter = new ListViewAdapter(Search.this, SearchedPersons);
                                list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                if (SearchedPersons.size() == 0) {
                                    if(isRoom == 0)
                                        SearchError.setVisibility(View.VISIBLE);
                                }
                                loading.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                            Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                        }
                    });
                    RoomReq.enqueue(new Callback<List<InputRoomSearchViewModel>>() {
                        @Override
                        public void onResponse(Call<List<InputRoomSearchViewModel>> call, Response<List<InputRoomSearchViewModel>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(Search.this, "unsuccessful", Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                            }
                            else {
                                for (InputRoomSearchViewModel room : response.body()) {
                                    SearchRoom R = new SearchRoom(room.getId(), room.getName(), room.getDescription(), room.getStartDate(), room.getInterests(), room.getMembersCount());
                                    SearchedRooms.add(R);
                                }

                                RoomAdapter = new RoomListViewAdapter(Search.this, SearchedRooms);
                                RoomList.setAdapter(RoomAdapter);
                                RoomAdapter.notifyDataSetChanged();

                                if (SearchedRooms.size() == 0) {
                                    if(isRoom == 1)
                                        SearchError.setVisibility(View.VISIBLE);
                                }
                                loading.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<InputRoomSearchViewModel>> call, Throwable t) {
                            Toast.makeText(Search.this, "Check your network", Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                        }
                    });
                    item_scroll.setVisibility(View.INVISIBLE);
                    SearchTitle.setText("Search in " + t);
                }

            });
        }
    }

    public CardView AddItem(String name, GridLayout layout) {

        CardView cardview = new CardView(this);

        LayoutParams layoutparams = new LayoutParams(
                450,
                LayoutParams.WRAP_CONTENT
        );
        layoutparams.setMargins(20, 25, 20, 25);
        cardview.setLayoutParams(layoutparams);
        cardview.setRadius(50);
        cardview.setPadding(25, 25, 25, 25);
        cardview.setCardBackgroundColor(Color.WHITE);
        cardview.setMaxCardElevation(6);
        TextView textview = new TextView(this);
        LayoutParams tlayoutparams = new LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT
        );
        tlayoutparams.setMargins(5, 15, 5, 15);
        textview.setLayoutParams(tlayoutparams);
        textview.setText(name);
        textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textview.setPadding(8, 8, 8, 8);
        textview.setGravity(Gravity.CENTER);
        cardview.addView(textview);
        layout.addView(cardview);
        return cardview;
    }

    public BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            item.setEnabled(false);
            switch (item.getItemId()) {
                case R.id.nav_home:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Search.this, com.example.chathouse.Pages.HomePage.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                    break;


                case R.id.nav_Profile:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(Search.this, com.example.chathouse.Pages.ProfilePage.class);
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

                            Intent intent = new Intent(Search.this, com.example.chathouse.Pages.Search.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;

                case R.id.nav_Activity:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(Search.this, AcitivityPage.class);
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

class ListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<SearchPerson> SearchedPersonsList = null;
    private ArrayList<SearchPerson> arraylist;

    public ListViewAdapter(Context context, List<SearchPerson> SearchedPersonsList) {
        mContext = context;
        this.SearchedPersonsList = SearchedPersonsList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<SearchPerson>();
        this.arraylist.addAll(SearchedPersonsList);

    }

    public class ViewHolder {
        TextView userName;
        TextView name;
        ImageView Image;

    }

    @Override
    public int getCount() {
        return SearchedPersonsList.size();
    }

    @Override
    public SearchPerson getItem(int position) {
        return SearchedPersonsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        holder = new ViewHolder();
        view = inflater.inflate(R.layout.list_view_searched_items, null);
        // Locate the TextViews in listview_item.xml
        holder.userName = (TextView) view.findViewById(R.id.itemUsername);
        holder.name = (TextView) view.findViewById(R.id.itemFirstNameAndLastName);
        holder.Image = (ImageView) view.findViewById(R.id.itemImage);
        view.setTag(holder);

        // Set the results into TextViews
        if (SearchedPersonsList.get(position).getFirstName() != null && SearchedPersonsList.get(position).getLastName() != null)
            holder.name.setText(SearchedPersonsList.get(position).getFirstName() + " " + SearchedPersonsList.get(position).getLastName());
        else
            holder.name.setVisibility(View.GONE);

        holder.userName.setText(SearchedPersonsList.get(position).getUsername());


        if (SearchedPersonsList.get(position).getImageLink() != null) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.default_user_profile)
                    .centerCrop();

            Glide.with(mContext).load(SearchedPersonsList.get(position).getImageLink())
                    .apply(options).transform(new CircleCrop()).into(holder.Image);
        }
        return view;
    }
}

//########################################################################


class RoomListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<SearchRoom> SearchedRoomsList = null;
    private ArrayList<SearchRoom> arraylist;

    public RoomListViewAdapter(Context context, List<SearchRoom> SearchedPersonsList) {
        mContext = context;
        this.SearchedRoomsList = SearchedPersonsList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<SearchRoom>();
        this.arraylist.addAll(SearchedRoomsList);

    }

    public class ViewHolder {
        TextView Name;
        TextView MembersCount;
        TextView Interest;
        TextView StartTime;

    }

    @Override
    public int getCount() {
        return SearchedRoomsList.size();
    }

    @Override
    public SearchRoom getItem(int position) {
        return SearchedRoomsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        holder = new ViewHolder();
        view = inflater.inflate(R.layout.list_view_room_searched_items, null);

        holder.Name = (TextView) view.findViewById(R.id.room_Name);
        holder.MembersCount = (TextView) view.findViewById(R.id.room_membersCount);
        holder.Interest = (TextView) view.findViewById(R.id.room_interest);
        holder.StartTime = (TextView) view.findViewById(R.id.room_startTime);

        view.setTag(holder);

        // Set the results into TextViews
        if (SearchedRoomsList.get(position).getName() != null )
            holder.Name.setText(SearchedRoomsList.get(position).getName());
        else
            holder.Name.setVisibility(View.GONE);

        holder.MembersCount.setText(String.valueOf(SearchedRoomsList.get(position).getMembersCount()) + " members");

        holder.Interest.setText(InterestName(SearchedRoomsList.get(position).getInterests()));

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm  dd MMMM yyyy");

        holder.StartTime.setText(formatter.format(SearchedRoomsList.get(position).getStartDate()));// todo show start time


        return view;
    }

    public String InterestName(ArrayList<ArrayList<Integer>> interests) {
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

class SearchRoom {
    private int Id;
    private String Name;
    private String Description;
    private Date StartDate;
    private ArrayList<ArrayList<Integer>> Interests;
    private int MembersCount;

    public SearchRoom(int id, String name, String description, Date startDate, ArrayList<ArrayList<Integer>> interests, int membersCount) {
        this.Id = id;
        this.Name = name;
        this.Description = description;
        this.StartDate = startDate;
        this.Interests = interests;
        this.MembersCount = membersCount;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public ArrayList<ArrayList<Integer>> getInterests() {
        return Interests;
    }

    public int getMembersCount() {
        return MembersCount;
    }

}