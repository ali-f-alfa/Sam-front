package com.example.chathouse.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
import com.example.chathouse.ViewModels.CreateRoomViewModel;
import com.example.chathouse.ViewModels.GetRoomViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        StartRoom = (Button) findViewById(R.id.StartRoom);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        loading.setVisibility(View.INVISIBLE);
        menu = (BottomNavigationView) findViewById(R.id.Home_menu);

        for (int i = 0; i < 14; i++) {
            SelectedInterest.add(new ArrayList<>());
        }



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




    }

    private CreateRoomViewModel RoomModel(String name, String description, Date startDate, Date endDate) {
        CreateRoomViewModel RoomModel = new CreateRoomViewModel(name, description, CreateInterests(), startDate, endDate);
        return RoomModel;
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

    public void Open(View view) {

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
        LinearLayoutCategory = (LinearLayout)dialog.findViewById(R.id.LinearLayoutCategory);
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
        EditText name = (EditText)dialog.findViewById(R.id.editNameRoom);
        EditText description = (EditText)dialog.findViewById(R.id.editDesRoom);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            CreateRoomViewModel Room = null;

            @Override
            public void onClick(View v) {
                if(Date != null){
                    Date = dateText.getText().toString() + "T" + timeText.getText().toString() + "Z";
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                    try {
                        RealDate = dateFormat.parse(Date);
                        Room = RoomModel(name.getText().toString(), description.getText().toString(), RealDate, null);
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Room = RoomModel(name.getText().toString(), description.getText().toString(), null, null);
                }

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
                        GetRoomViewModel Response = response.body();
                        if(Date == null){
                            Intent intent = new Intent(HomePage.this, com.example.chathouse.Pages.Room.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("RoomId", Response.getId());
                            bundle.putString("Creator", Response.getCreator().getUsername());
                            bundle.putString("Name", Response.getName());
                            intent.putExtras(bundle);
//                            intent.putExtra("GetRoom", Response);
                            startActivity(intent);
                        }
                        else{
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
}
