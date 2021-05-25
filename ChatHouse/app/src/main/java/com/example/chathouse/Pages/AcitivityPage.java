package com.example.chathouse.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.RoomModel;
import com.example.chathouse.ViewModels.Acount.UpdateRoomViewModel;
import com.example.chathouse.ViewModels.Room.GetRoomViewModel;
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

public class AcitivityPage extends AppCompatActivity {

    public String Username;
    private GridLayout category_grid_modal;
    private ScrollView category_scroll_modal;
    private GridLayout item_grid_modal;
    private HorizontalScrollView item_scroll_modal;
    private ArrayList<ArrayList<Integer>> SelectedInterest = new ArrayList<>();
    private LinearLayout LinearLayoutCategory;
    private TextView interestName;
    private RadioButton Now;
    private RadioButton Schedule;
    private String datex;
    private int Cat;
    private int item;
    private SimpleDateFormat dateFormat;
    private java.util.Date RealDate;
    private BottomNavigationView menu;
    private ProgressBar loading;
    private ChatHouseAPI APIS;

    private ListView RoomsList;
    private x RoomsAdapter;
    private ArrayList<RoomModel> RoomsModelListArray = new ArrayList<RoomModel>();

    private ListView RoomsListIn;
    private x RoomsAdapterIn;
    private ArrayList<RoomModel> RoomsModelListArrayIn = new ArrayList<RoomModel>();

    private boolean selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Date objDate = new Date();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acitivity_page);
        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        Username = settings.getString("Username", "n/a");
        menu = (BottomNavigationView) findViewById(R.id.Activity_menu);
        RoomsList = (ListView)findViewById(R.id.RoomsInactivity);
        RoomsListIn = (ListView)findViewById(R.id.RoomsInactivityIn);
        loading = (ProgressBar)findViewById(R.id.progressBar);

        menu.setOnNavigationItemSelectedListener(navListener);
        int a = menu.getSelectedItemId();
        selected = false;

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

        for (int i = 0; i < 14; i++) {
            SelectedInterest.add(new ArrayList<>());
        }


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

        Call<ProfileInformation> GetProfile = APIS.GetProfile(Username);

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
                loading.setVisibility(View.INVISIBLE);

                // Set the values from back
                ProfileInformation Response = response.body();
                for (RoomModel person : Response.getCreatedRooms()) {
                    RoomModel Person = new RoomModel(person.getRoomId(), person.getName(), person.getDescription(),  person.getStartDate(), person.getEndDate(), person.getInterests(), person.getCreator(), person.getMembers());
                    if(person.getEndDate().compareTo(objDate) > 0){
                        RoomsModelListArray.add(Person);
                    }
                }

                RoomsAdapter = new x(AcitivityPage.this, RoomsModelListArray);
                RoomsList.setAdapter(RoomsAdapter);

                for (RoomModel person : Response.getInRooms()) {
                    RoomModel Person = new RoomModel(person.getRoomId(), person.getName(), person.getDescription(),  person.getStartDate(), person.getEndDate(), person.getInterests(), person.getCreator(), person.getMembers());
                    if(person.getEndDate().compareTo(objDate) > 0){
                        RoomsModelListArrayIn.add(Person);
                    }
                }

                RoomsAdapterIn = new x(AcitivityPage.this, RoomsModelListArrayIn);
                RoomsListIn.setAdapter(RoomsAdapterIn);

            }

            @Override
            public void onFailure(Call<ProfileInformation> call, Throwable t) {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(AcitivityPage.this, "Request failed" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });




        RoomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomModel p = (RoomModel) RoomsList.getAdapter().getItem(position);
                if(p.getStartDate().compareTo(objDate) < 0){
                    Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.Pages.Room.class);
                    Bundle bundle = new Bundle();

                    int idx = p.getRoomId();
                    bundle.putInt("RoomId", idx);
//                    bundle.putString("Name", p.getName());
//                    bundle.putString("Creator", p.getCreator().getUsername());
                    System.out.println(p.getCreator());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else{
                    Open(view, p);
                    System.out.println(p.getRoomId());
                }
            }
        });

        RoomsListIn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomModel p = (RoomModel) RoomsListIn.getAdapter().getItem(position);
                if(p.getStartDate().compareTo(objDate) < 0){
                    Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.Pages.Room.class);
                    Bundle bundle = new Bundle();

                    int idx = p.getRoomId();
                    bundle.putInt("RoomId", idx);
                    bundle.putString("Name", p.getName());
                    bundle.putString("Creator", p.getCreator().getUsername());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else{
                    // Show them count down
                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm  dd MMMM yyyy");
                    alert.setMessage("Room start time is: \n" + formatter.format(p.getStartDate()));

                    alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!

                                }
                            })
                            .setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    Call<Void> LeaveRoom = APIS.LeaveRoom(p.getRoomId());
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
                                            RoomsModelListArrayIn.remove(position);
                                            RoomsAdapterIn.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(AcitivityPage.this, "Request failed" , Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });

                    alert.show();
                }
            }
        });


    }



    public void Open(View view, RoomModel p) {

        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_room_creation);
        category_grid_modal = (GridLayout) dialog.findViewById(R.id.category_grid_modal);
        category_scroll_modal = (ScrollView) dialog.findViewById(R.id.Category_modal);
        item_grid_modal = (GridLayout) dialog.findViewById(R.id.item_grid_modal);
        item_scroll_modal = (HorizontalScrollView) dialog.findViewById(R.id.Items_modal);
        interestName = (TextView) dialog.findViewById(R.id.interestEditmodal);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        loading.setVisibility(View.INVISIBLE);
        EditText name = (EditText)dialog.findViewById(R.id.editNameRoom);
        EditText description = (EditText)dialog.findViewById(R.id.editDesRoom);

        Button dialogButton = (Button) dialog.findViewById(R.id.Create);
        dialogButton.setText("Update");
        Button date = (Button) dialog.findViewById(R.id.btn_date);
        EditText dateText = (EditText) dialog.findViewById(R.id.in_date);
        Button time = (Button) dialog.findViewById(R.id.btn_time);
        EditText timeText = (EditText) dialog.findViewById(R.id.in_time);
        TextView finalDate = (TextView)dialog.findViewById(R.id.timeEditmodal);

        Button delete = (Button) dialog.findViewById(R.id.Delete);

        LinearLayoutCategory = (LinearLayout)dialog.findViewById(R.id.LinearLayoutCategory);
        Now = dialog.findViewById(R.id.Now);
        Schedule = dialog.findViewById(R.id.Schedule);

        interestName.setText(InterestName(p.getInterests()));

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm  dd MMMM yyyy");
        finalDate.setText(formatter.format(p.getStartDate()));

        name.setText(p.getName());
        description.setText(p.getDescription());

//        dateText.setText(p.getStartDate().getYear() + '-' + p.getStartDate().getMonth() + '-' + p.getStartDate().getDay());
//        timeText.setText(p.getStartDate().getHours() + '-' + p.getStartDate().getMinutes());


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

        Now.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                datex = null;
                date.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                dateText.setVisibility(View.GONE);
                timeText.setVisibility(View.GONE);
                selected = true;
            }

        });
        Schedule.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                datex = "";
                date.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                dateText.setVisibility(View.VISIBLE);
                timeText.setVisibility(View.VISIBLE);
                selected = true;
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


                DatePickerDialog datePickerDialog = new DatePickerDialog(AcitivityPage.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dateText.setText(year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth);

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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AcitivityPage.this,
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

        dialogButton.setOnClickListener(new View.OnClickListener() {
            UpdateRoomViewModel Room = null;

            @Override
            public void onClick(View v) {
                if(datex != null){
                    datex = dateText.getText().toString() + "T" + timeText.getText().toString() + "Z";
                    finalDate.setText(dateText.getText().toString() + " " + timeText.getText().toString());
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                    try {
                        RealDate = dateFormat.parse(datex);
                        Room = UpdateRoom(name.getText().toString(), description.getText().toString(), RealDate, null, p.getRoomId(), SelectedInterest);
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Date now = new Date();
                    if(selected){
                        Room = UpdateRoom(name.getText().toString(), description.getText().toString(), now, null, p.getRoomId(), SelectedInterest);
                    }
                    else{
                        Room = UpdateRoom(name.getText().toString(), description.getText().toString(), null, null, p.getRoomId(), SelectedInterest);
                    }
                }
                System.out.println("Interest" + SelectedInterest);

                Call<GetRoomViewModel> UpdateRoomAPI = APIS.UpdateRoom(Room);

                loading.setVisibility(View.VISIBLE);
                UpdateRoomAPI.enqueue(new Callback<GetRoomViewModel>() {
                    @Override
                    public void onResponse(Call<GetRoomViewModel> call, Response<GetRoomViewModel> response) {
                        if(!response.isSuccessful()){
                            try {
                                loading.setVisibility(View.INVISIBLE);
                                System.out.println("1" + response.errorBody().string());
                                System.out.println(response.errorBody().string());
                            } catch (IOException e) {
                                loading.setVisibility(View.INVISIBLE);
                                System.out.println("2" + response.errorBody().toString());

                                e.printStackTrace();
                            }
                            return;
                        }
                        loading.setVisibility(View.INVISIBLE);
                        System.out.println("Room just Updated Okay" + response.body());
                        GetRoomViewModel Response = response.body();
                        finish();

                        Date objDate = new Date();
                        if(p.getStartDate().compareTo(objDate) <= 0){

//                            new Handler().post(new Runnable() {
//                                @Override
//                                public void run() {
                                    Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.Pages.Room.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("RoomId", Response.getId());
                                    bundle.putString("Creator", Response.getCreator().getUsername());
                                    bundle.putString("Name", Response.getName());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
//                                    finish();
//                                }
//                            });
                        }
                        else{
//                            new Handler().post(new Runnable() {
//                                @Override
//                                public void run() {
                            startActivity(getIntent());
//                                    finish();
//                                }
//                            });

                        }
                    }

                    @Override
                    public void onFailure(Call<GetRoomViewModel> call, Throwable t) {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(AcitivityPage.this, "Request failed", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            Call<Void> DeleteRoom = APIS.DeleteRoom(p.getRoomId());
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
                        Intent intent = new Intent(AcitivityPage.this, AcitivityPage.class);

                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AcitivityPage.this, "Request failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        dialog.show();
    }

    private UpdateRoomViewModel UpdateRoom(String toString, String toString1, Date realDate, Date o, Integer roomId, ArrayList<ArrayList<Integer>> selectedInterest) {
        UpdateRoomViewModel r = new UpdateRoomViewModel(roomId, toString, toString1, realDate, o,  CreateInterests());
        return r;
    }

    private ArrayList<ArrayList<Integer>> CreateInterests() {
        for (int i = 0; i < 14; i++) {
            if(!SelectedInterest.get(i).isEmpty()){
                SelectedInterest.get(i).remove(0);
            };
        }
        SelectedInterest.get(Cat).add((int) Math.pow(2, item));

        return SelectedInterest;
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
            String itemName = temp.get(x);

            AddItem(temp.get(x), item_grid_modal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    item_scroll_modal.setVisibility(View.INVISIBLE);
                    Cat = n;
                    item = xx;
                    CardView t = (CardView)v;
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

    public BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            item.setEnabled(false);
            switch (item.getItemId()) {
                case R.id.nav_home:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.Pages.HomePage.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                    break;


                case R.id.nav_Profile:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.Pages.ProfilePage.class);
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

                            Intent intent = new Intent(AcitivityPage.this, com.example.chathouse.Pages.Search.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;

                case R.id.nav_Activity:
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(AcitivityPage.this, AcitivityPage.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;
            }
            return false;
        }
    };

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


class x extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<RoomModel> SearchedPersonsList = null;
    private ArrayList<RoomModel> arraylist;

    public x(Context context, List<RoomModel> SearchedPersonsList) {
        mContext = context;
        this.SearchedPersonsList = SearchedPersonsList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<RoomModel>();
        this.arraylist.addAll(SearchedPersonsList);

    }

    public class ViewHolder {
        TextView name;
        TextView description;
        TextView interest;

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

    @Override
    public int getCount() {
        return SearchedPersonsList.size();
    }

    @Override
    public RoomModel getItem(int position) {
        return SearchedPersonsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final com.example.chathouse.Pages.x.ViewHolder holder;
        holder = new ViewHolder();
        view = inflater.inflate(R.layout.rooms_list_in_activities, null);
        // Locate the TextViews in listview_item.xml
        holder.description = (TextView) view.findViewById(R.id.RoomDescriptionActivity);
        holder.name = (TextView) view.findViewById(R.id.RoomNameActivity);
        holder.interest = (TextView) view.findViewById(R.id.RoomInterestActivity);
        view.setTag(holder);

        // Set the results into TextViews
        if (SearchedPersonsList.get(position).getName() != null)
            holder.name.setText(SearchedPersonsList.get(position).getName());
        else
            holder.name.setVisibility(View.GONE);

        holder.description.setText(SearchedPersonsList.get(position).getDescription());

        holder.interest.setText(InterestName(SearchedPersonsList.get(position).getInterests()));



        return view;
    }
}