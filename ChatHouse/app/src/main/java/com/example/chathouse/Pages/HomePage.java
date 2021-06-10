package com.example.chathouse.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Room.CreateRoomViewModel;
import com.example.chathouse.ViewModels.Room.GetRoomViewModel;
import com.example.chathouse.ViewModels.Search.InputRoomSearchViewModel;
import com.example.chathouse.ViewModels.Search.InputRoomSuggestSearchViewModel;
import com.example.chathouse.ViewModels.Search.InputSearchViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private GridLayout category_grid_modal;
    private ScrollView category_scroll_modal;
    private GridLayout item_grid_modal;
    private HorizontalScrollView item_scroll_modal;
    private ArrayList<ArrayList<Integer>> SelectedInterest = new ArrayList<>();
    private int Cat;
    private int item;
    private LinearLayout LinearLayoutCategory;
    private TextView interestName;
    private RadioButton Now;
    private RadioButton Schedule;
    private String Date;
    private SimpleDateFormat dateFormat;
    private Date RealDate;

    ListView SuggestedRoomsByInterestsList;
    ListView SuggestedRoomsByFollowingsList;
    SuggestedRoomsByInterestsListViewAdapter adapter1;
    SuggestedRoomsByFollowingsListViewAdapter adapter2;
    ArrayList<InputRoomSearchViewModel> SearchedRoomsInterests = new ArrayList<InputRoomSearchViewModel>();
    ArrayList<InputRoomSuggestSearchViewModel> SearchedRoomsFollowings = new ArrayList<InputRoomSuggestSearchViewModel>();
    int i = 3;
    int r = 3;
    boolean Room1EndOfList;
    boolean Room2EndOfList;
    TextView SearchError1;
    TextView SearchError2;
    SharedPreferences settings;
    LinearLayout layout;

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
        setContentView(R.layout.activity_home_page);
        StartRoom = (Button) findViewById(R.id.StartRoom);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        SuggestedRoomsByInterestsList = (ListView) findViewById(R.id.SuggestRoomsInterests);
        SuggestedRoomsByFollowingsList = (ListView) findViewById(R.id.SuggestRoomsFollowings);
        SearchError1 = findViewById(R.id.SearchError1);
        SearchError2 = findViewById(R.id.SearchError2);
        layout = (LinearLayout)findViewById(R.id.Homeback);

        if (themeName.equalsIgnoreCase("DarkTheme")) {
            layout.setBackgroundResource(R.drawable.b22d);
        } else if (themeName.equalsIgnoreCase("Theme")) {
            layout.setBackgroundResource(R.drawable.b22);
        }

        menu = (BottomNavigationView) findViewById(R.id.Home_menu);

        for (int i = 0; i < 14; i++) {
            SelectedInterest.add(new ArrayList<>());
        }


        settings = getSharedPreferences("Storage", MODE_PRIVATE);
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
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
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

        StartRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Open(v);
            }
        });

        /////////////////////////////

        SuggestedRoomsByInterestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                InputRoomSearchViewModel R = (InputRoomSearchViewModel) SuggestedRoomsByInterestsList.getAdapter().getItem(position);
                Call<ProfileInformation> JoinRoom = APIS.JoinRoom(R.getId());
                JoinRoom.enqueue(new Callback<ProfileInformation>() {
                    @Override
                    public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(HomePage.this, "join request was not successful ", Toast.LENGTH_SHORT).show();

                            return;
                        }
                        Toast.makeText(HomePage.this, "successfully joined the room", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(HomePage.this, com.example.chathouse.Pages.Room.class);
                        Bundle bundle = new Bundle();

                        bundle.putInt("RoomId", R.getId());
                        bundle.putString("Name", R.getName());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ProfileInformation> call, Throwable t) {
                        Toast.makeText(HomePage.this, "request failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        SuggestedRoomsByFollowingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputRoomSuggestSearchViewModel R = (InputRoomSuggestSearchViewModel) SuggestedRoomsByFollowingsList.getAdapter().getItem(position);
                Call<ProfileInformation> JoinRoom = APIS.JoinRoom(R.getId());
                JoinRoom.enqueue(new Callback<ProfileInformation>() {
                    @Override
                    public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(HomePage.this, "join request was not successful ", Toast.LENGTH_SHORT).show();

                            return;
                        }
                        Toast.makeText(HomePage.this, "successfully joined the room", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(HomePage.this, com.example.chathouse.Pages.Room.class);
                        Bundle bundle = new Bundle();

                        bundle.putInt("RoomId", R.getId());
                        bundle.putString("Name", R.getName());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ProfileInformation> call, Throwable t) {
                        Toast.makeText(HomePage.this, "request failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        Call<List<InputRoomSearchViewModel>> RoomSuggestInterests = APIS.RoomSuggestByInterests(10, 1);
        loading.setVisibility(View.VISIBLE);
        RoomSuggestInterests.enqueue(new Callback<List<InputRoomSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputRoomSearchViewModel>> call, Response<List<InputRoomSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(HomePage.this, "request was not successful ", Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.GONE);
                } else {
                    for (InputRoomSearchViewModel room : response.body()) {
                        SearchedRoomsInterests.add(room);
                    }

                    adapter1 = new SuggestedRoomsByInterestsListViewAdapter(HomePage.this, SearchedRoomsInterests);
                    SuggestedRoomsByInterestsList.setAdapter(adapter1);
                    adapter1.notifyDataSetChanged();

                    if (SearchedRoomsInterests.size() == 0)
                        SearchError1.setVisibility(View.VISIBLE);

                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<InputRoomSearchViewModel>> call, Throwable t) {
                Toast.makeText(HomePage.this, "Check your network", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);

            }
        });

        Call<List<InputRoomSuggestSearchViewModel>> RoomSuggestFollowings = APIS.RoomSuggestByFollowings(10, 1);
        loading.setVisibility(View.VISIBLE);
        RoomSuggestFollowings.enqueue(new Callback<List<InputRoomSuggestSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputRoomSuggestSearchViewModel>> call, Response<List<InputRoomSuggestSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(HomePage.this, "request was not successful ", Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.GONE);
                } else {
                    for (InputRoomSuggestSearchViewModel room : response.body()) {
                        SearchedRoomsFollowings.add(room);
                    }

                    adapter2 = new SuggestedRoomsByFollowingsListViewAdapter(HomePage.this, SearchedRoomsFollowings);
                    SuggestedRoomsByFollowingsList.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();

                    if (SearchedRoomsFollowings.size() == 0)
                        SearchError2.setVisibility(View.VISIBLE);

                    loading.setVisibility(View.GONE);

                }

            }

            @Override
            public void onFailure(Call<List<InputRoomSuggestSearchViewModel>> call, Throwable t) {
                Toast.makeText(HomePage.this, "Check your network", Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);

            }
        });

        //pagination
        SuggestedRoomsByInterestsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (!Room1EndOfList) {
                    if (SuggestedRoomsByInterestsList.getLastVisiblePosition() == SuggestedRoomsByInterestsList.getAdapter().getCount() - 1 &&
                            SuggestedRoomsByInterestsList.getChildAt(SuggestedRoomsByInterestsList.getChildCount() - 1).getBottom() <= SuggestedRoomsByInterestsList.getHeight()) { //end of scroll


                        Call<List<InputRoomSearchViewModel>> RoomReq = APIS.RoomSuggestByInterests(5, i++);

                        loading.setVisibility(View.VISIBLE);

                        RoomReq.enqueue(new Callback<List<InputRoomSearchViewModel>>() {
                            @Override
                            public void onResponse(Call<List<InputRoomSearchViewModel>> call, Response<List<InputRoomSearchViewModel>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(HomePage.this, "request was not successful ", Toast.LENGTH_LONG).show();
                                    loading.setVisibility(View.GONE);
                                } else {

                                    if (response.body().size() == 0)
                                        Room1EndOfList = true;

                                    for (InputRoomSearchViewModel room : response.body()) {
                                        SearchedRoomsInterests.add(room);
                                    }

                                    adapter1.notifyDataSetChanged();


                                    if (SearchedRoomsInterests.size() == 0) {
//                                        SearchError.setVisibility(View.VISIBLE);
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<InputRoomSearchViewModel>> call, Throwable t) {
                                Toast.makeText(HomePage.this, "Check your network", Toast.LENGTH_LONG).show();
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
        SuggestedRoomsByFollowingsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (!Room2EndOfList) {
                    if (SuggestedRoomsByFollowingsList.getLastVisiblePosition() == SuggestedRoomsByFollowingsList.getAdapter().getCount() - 1 &&
                            SuggestedRoomsByFollowingsList.getChildAt(SuggestedRoomsByFollowingsList.getChildCount() - 1).getBottom() <= SuggestedRoomsByFollowingsList.getHeight()) { //end of scroll


                        Call<List<InputRoomSuggestSearchViewModel>> RoomReq = APIS.RoomSuggestByFollowings(5, i++);

                        loading.setVisibility(View.VISIBLE);

                        RoomReq.enqueue(new Callback<List<InputRoomSuggestSearchViewModel>>() {
                            @Override
                            public void onResponse(Call<List<InputRoomSuggestSearchViewModel>> call, Response<List<InputRoomSuggestSearchViewModel>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(HomePage.this, "request was not successful ", Toast.LENGTH_LONG).show();
                                    loading.setVisibility(View.GONE);
                                } else {

                                    if (response.body().size() == 0)
                                        Room2EndOfList = true;

                                    for (InputRoomSuggestSearchViewModel room : response.body()) {
                                        SearchedRoomsFollowings.add(room);
                                    }

                                    adapter2.notifyDataSetChanged();


                                    if (SearchedRoomsFollowings.size() == 0) {
//                                        SearchError.setVisibility(View.VISIBLE);
                                    }
                                    loading.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<InputRoomSuggestSearchViewModel>> call, Throwable t) {
                                Toast.makeText(HomePage.this, "Check your network", Toast.LENGTH_LONG).show();
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

    }

    private CreateRoomViewModel RoomModel(String name, String description, Date startDate, Date endDate) {
        CreateRoomViewModel RoomModel = new CreateRoomViewModel(name, description, CreateInterests(), startDate, endDate);
        return RoomModel;
    }

    private ArrayList<ArrayList<Integer>> CreateInterests() {
        for (int i = 0; i < 14; i++) {
            if (!SelectedInterest.get(i).isEmpty()) {
                SelectedInterest.get(i).remove(0);
            }
            ;
        }
        SelectedInterest.get(Cat).add((int) Math.pow(2, item));

        return SelectedInterest;
    }

    public void Open(View view) {

        settings  = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String themeName = settings.getString("ThemeName", "DarkTheme");
        if (themeName.equalsIgnoreCase("DarkTheme")) {
            setTheme(R.style.DarkTheme_ChatHouse);
        } else if (themeName.equalsIgnoreCase("Theme")) {
            setTheme(R.style.Theme_ChatHouse);
        }
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_room_creation);
        category_grid_modal = (GridLayout) dialog.findViewById(R.id.category_grid_modal);
        category_scroll_modal = (ScrollView) dialog.findViewById(R.id.Category_modal);
        item_grid_modal = (GridLayout) dialog.findViewById(R.id.item_grid_modal);
        item_scroll_modal = (HorizontalScrollView) dialog.findViewById(R.id.Items_modal);
        interestName = (TextView) dialog.findViewById(R.id.interestEditmodal);

        int childCount = category_grid_modal.getChildCount();
        for (int i = 0; i < childCount; i++) {
            int n = i;
            CardView container = (CardView) category_grid_modal.getChildAt(i);
            container.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    category_scroll_modal.setVisibility(View.INVISIBLE);
                    CreateItems(n, item_grid_modal);
                }
            });
        }


        Button dialogButton = (Button) dialog.findViewById(R.id.Create);
        Button delete = (Button) dialog.findViewById(R.id.Delete);
        delete.setVisibility(View.GONE);
        Button date = (Button) dialog.findViewById(R.id.btn_date);
        EditText dateText = (EditText) dialog.findViewById(R.id.in_date);
        Button time = (Button) dialog.findViewById(R.id.btn_time);
        EditText timeText = (EditText) dialog.findViewById(R.id.in_time);
        LinearLayoutCategory = (LinearLayout) dialog.findViewById(R.id.LinearLayoutCategory);
        Now = dialog.findViewById(R.id.Now);
        Schedule = dialog.findViewById(R.id.Schedule);
        date.setVisibility(View.GONE);
        time.setVisibility(View.GONE);
        dateText.setVisibility(View.GONE);
        timeText.setVisibility(View.GONE);

        Now.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date = null;
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                dateText.setVisibility(View.GONE);
                timeText.setVisibility(View.GONE);
            }

        });
        Schedule.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date = "";
                date.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                dateText.setVisibility(View.VISIBLE);
                timeText.setVisibility(View.VISIBLE);
            }

        });


        date.setOnClickListener(new View.OnClickListener() {
            private int mYear, mMonth, mDay;

            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(HomePage.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dateText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            private int mHour, mMinute;

            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomePage.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                timeText.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        // if button is clicked, close the custom dialog
        EditText name = (EditText) dialog.findViewById(R.id.editNameRoom);
        EditText description = (EditText) dialog.findViewById(R.id.editDesRoom);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            CreateRoomViewModel Room = null;

            @Override
            public void onClick(View v) {
                if (Date != null) {
                    Date = dateText.getText().toString() + "T" + timeText.getText().toString() + "Z";
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                    try {
                        RealDate = dateFormat.parse(Date);
                        Room = RoomModel(name.getText().toString(), description.getText().toString(), RealDate, null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Room = RoomModel(name.getText().toString(), description.getText().toString(), null, null);
                }

                CreateRoom = APIS.CreateRoom(Room);
                loading.setVisibility(View.VISIBLE);
                CreateRoom.enqueue(new Callback<GetRoomViewModel>() {
                    @Override
                    public void onResponse(Call<GetRoomViewModel> call, Response<GetRoomViewModel> response) {
                        if (!response.isSuccessful()) {
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
                        GetRoomViewModel Response = response.body();
                        if (Date == null) {
                            Intent intent = new Intent(HomePage.this, com.example.chathouse.Pages.Room.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("RoomId", Response.getId());
                            bundle.putString("Creator", Response.getCreator().getUsername());
                            bundle.putString("Name", Response.getName());
                            intent.putExtras(bundle);
//                            intent.putExtra("GetRoom", Response);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(HomePage.this, AcitivityPage.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("RoomId", Response.getId());
                            bundle.putString("Creator", Response.getCreator().getUsername());
                            bundle.putString("Name", Response.getName());
//                            intent.putExtra("GetRoom", Response);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
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

                case R.id.nav_Activity:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(HomePage.this, AcitivityPage.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;
            }
            return false;
        }
    };

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
            String itemName = temp.get(x);

            AddItem(temp.get(x), item_grid_modal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    item_scroll_modal.setVisibility(View.INVISIBLE);
                    Cat = n;
                    item = xx;
                    CardView t = (CardView) v;
                    ((CardView) v).setCardBackgroundColor(Color.GRAY);
                    LinearLayoutCategory.setVisibility(View.GONE);
                    interestName.setText(itemName);
                }

            });
        }
    }

    public CardView AddItem(String name, GridLayout layout) {

        CardView cardview = new CardView(this);

        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                450,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutparams.setMargins(20, 25, 20, 25);
        cardview.setLayoutParams(layoutparams);
        cardview.setRadius(50);
        cardview.setPadding(25, 25, 25, 25);
        cardview.setCardBackgroundColor(Color.WHITE);
        cardview.setMaxCardElevation(6);
        TextView textview = new TextView(this);
        LinearLayout.LayoutParams tlayoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT
        );
        tlayoutparams.setMargins(5, 15, 5, 15);
        textview.setLayoutParams(tlayoutparams);
        textview.setText(name);
        textview.setTextColor(Color.WHITE);
        textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textview.setPadding(8, 8, 8, 8);
        textview.setGravity(Gravity.CENTER);
        cardview.addView(textview);
        cardview.setCardBackgroundColor(Color.parseColor("#E98980"));
        layout.addView(cardview);
        return cardview;
    }
}


class SuggestedRoomsByInterestsListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<InputRoomSearchViewModel> SearchedRoomsList = null;

    public SuggestedRoomsByInterestsListViewAdapter(Context context, List<InputRoomSearchViewModel> SearchedPersonsList) {
        mContext = context;
        this.SearchedRoomsList = SearchedPersonsList;
        inflater = LayoutInflater.from(mContext);

    }

    public class ViewHolder {
        TextView Name;
        TextView MembersCount;
        TextView Interest;
        TextView Description;

    }

    @Override
    public int getCount() {
        return SearchedRoomsList.size();
    }

    @Override
    public InputRoomSearchViewModel getItem(int position) {
        return SearchedRoomsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        holder = new ViewHolder();
        view = inflater.inflate(R.layout.list_view_room_interests_suggested_items, null);

        holder.Name = (TextView) view.findViewById(R.id.room_Name_interests);
        holder.MembersCount = (TextView) view.findViewById(R.id.room_membersCount_interests);
        holder.Interest = (TextView) view.findViewById(R.id.room_interest_interests);
        holder.Description = (TextView) view.findViewById(R.id.room_description_interests);

        view.setTag(holder);

        // Set the results into TextViews
        if (SearchedRoomsList.get(position).getName() != null)
            holder.Name.setText(SearchedRoomsList.get(position).getName());
        else
            holder.Name.setVisibility(View.GONE);

        holder.MembersCount.setText(String.valueOf(SearchedRoomsList.get(position).getMembersCount()) + " members");

        holder.Interest.setText(InterestName(SearchedRoomsList.get(position).getInterests()));

        holder.Description.setText(SearchedRoomsList.get(position).getDescription());


        return view;
    }

    public String InterestName(ArrayList<ArrayList<Integer>> interests) {
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

}


class SuggestedRoomsByFollowingsListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<InputRoomSuggestSearchViewModel> SearchedRoomsList = null;

    public SuggestedRoomsByFollowingsListViewAdapter(Context context, List<InputRoomSuggestSearchViewModel> SearchedPersonsList) {
        mContext = context;
        this.SearchedRoomsList = SearchedPersonsList;
        inflater = LayoutInflater.from(mContext);

    }

    public class ViewHolder {
        TextView Name;
        TextView MembersCount;
        TextView Interest;
        TextView Description;
        TextView SuggestedBy;

    }

    @Override
    public int getCount() {
        return SearchedRoomsList.size();
    }

    @Override
    public InputRoomSuggestSearchViewModel getItem(int position) {
        return SearchedRoomsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        holder = new ViewHolder();
        view = inflater.inflate(R.layout.list_view_room_followings_suggested_items, null);

        holder.Name = (TextView) view.findViewById(R.id.room_Name_followings);
        holder.MembersCount = (TextView) view.findViewById(R.id.room_membersCount_followings);
        holder.Interest = (TextView) view.findViewById(R.id.room_interest_followings);
        holder.SuggestedBy = (TextView) view.findViewById(R.id.room_suggestedBy_followings);
        holder.Description = (TextView) view.findViewById(R.id.room_description_followings);

        view.setTag(holder);

        // Set the results into TextViews
        if (SearchedRoomsList.get(position).getName() != null)
            holder.Name.setText(SearchedRoomsList.get(position).getName());
        else
            holder.Name.setVisibility(View.GONE);

        holder.MembersCount.setText(String.valueOf(SearchedRoomsList.get(position).getMembersCount()) + " members");

        holder.SuggestedBy.setText(CreateSuggestedByString(SearchedRoomsList.get(position).getSuggestBy()) + "  follows this room");

        holder.Description.setText(SearchedRoomsList.get(position).getDescription());

        holder.Interest.setText(InterestName(SearchedRoomsList.get(position).getInterests()));

        return view;
    }

    public String CreateSuggestedByString(ArrayList<InputSearchViewModel> users) {
        String result = "";
        for (int x = 0; x < users.size(); x++) {
            if (x != users.size() - 1)
                result += users.get(x).getUsername() + " and ";
            else
                result += users.get(x).getUsername();
        }
        return result;

    }

    public String InterestName(ArrayList<ArrayList<Integer>> interests) {
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

}