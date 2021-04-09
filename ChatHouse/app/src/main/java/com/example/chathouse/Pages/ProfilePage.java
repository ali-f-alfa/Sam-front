package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.FollowingFollowers;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class ProfilePage extends AppCompatActivity {
    private TextView LastName;
    private TextView FirstName;
    private TextView UserName;
    private TextView Description;
    private TextView FollowersNumber;
    private TextView FollowingNumber;
    private TextView Followers;
    private TextView Following;
    private Button Message;
    private Button Follow;
    private ImageView ProfilePicture;
    //    private TextView Memberof;
    private Button EditProfile;
    private LinearLayout InterestContainer;
    private TextView UsernameText;
    private TextView EmailText;
    private HorizontalScrollView Interests;
    private ArrayList<FollowingFollowers> FollowersList = new ArrayList<>();
    private ArrayList<FollowingFollowers> FollowingList = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> ResponseInterests = new ArrayList<>();
    private ListView FollowingFollowersListView;
    private String ImageLink;
    private ProgressBar loading;
    private ConstraintLayout Fake;
    private Button SearchButton;
//    private Switch OnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Fake = (ConstraintLayout) findViewById(R.id.Fake);
        UserName = (EditText) findViewById(R.id.Username);
        ProfilePicture = (ImageView) findViewById(R.id.ProfileImage);
        LastName = (TextView) findViewById(R.id.LastName);
        FirstName = (TextView) findViewById(R.id.FirstName);
        Description = (TextView) findViewById(R.id.Bio);
        FollowersNumber = (TextView) findViewById(R.id.Followers);
        FollowingNumber = (TextView) findViewById(R.id.Following);
        Followers = (TextView) findViewById(R.id.FollowersText);
        Following = (TextView) findViewById(R.id.FollowingText);
        Message = (Button) findViewById(R.id.MessageButton);
        Follow = (Button) findViewById(R.id.FollowButton);
//        Memberof = (TextView)findViewById(R.id.MemberOf);
        UsernameText = (TextView) findViewById(R.id.UsernameText);
        EmailText = (TextView) findViewById(R.id.EmailText);
        EditProfile = (Button) findViewById(R.id.EditProfileButton);
        InterestContainer = (LinearLayout) findViewById(R.id.ContainerButton);
        Interests = (HorizontalScrollView) findViewById(R.id.Interests);
        FollowingFollowersListView = (ListView) findViewById(R.id.FollowingFollowersListView);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        SearchButton = (Button) findViewById(R.id.SearchButton);
//        OnOff = (Switch)findViewById(R.id.InterestsOnOff);
//
//        OnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    Interests.setVisibility(View.INVISIBLE);
//                }
//                else{
//                    Interests.setVisibility(View.VISIBLE);
//                }
//            }
//        });

//        Memberof.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        Fake.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.GONE);

        loading.setVisibility(View.VISIBLE);
        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);


        String Token = settings.getString("Token", "n/a");
//        String Username = settings.getString("Username", "n/a");

        String UsernameX = bundle.getString("Username");
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
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUrl.get(Constants.baseURL))
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory((GsonConverterFactory.create(gson)))
                .build();
        ChatHouseAPI GetProfileAPI = retrofit.create(ChatHouseAPI.class);

        Call<ProfileInformation> GetProfile = GetProfileAPI.GetProfile(UsernameX);

        GetProfile.enqueue(new Callback<ProfileInformation>() {
            @Override
            public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                if (!response.isSuccessful()) {
                    try {
                        FirstName.setText(response.errorBody().string());
                    } catch (IOException e) {
                        FirstName.setText(response.errorBody().toString());

                        e.printStackTrace();
                    }
                    return;
                }

                // Set the values from back
                ProfileInformation Response = response.body();


                if (Response.getMe()) {
                    Message.setVisibility(View.INVISIBLE);
                    Follow.setVisibility(View.INVISIBLE);
                    EditProfile.setVisibility(View.VISIBLE);
//                    OnOff.setVisibility(View.VISIBLE);
                } else {
                    Message.setVisibility(View.VISIBLE);
                    Follow.setVisibility(View.VISIBLE);
                    EditProfile.setVisibility(View.INVISIBLE);
                }

                try {
                    SetInformation(Response);
                    Fake.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ProfileInformation> call, Throwable t) {
                FirstName.setText(t.getMessage().toString());
                Toast.makeText(ProfilePage.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ProfilePage.this, Search.class);

                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
        EditProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, com.example.chathouse.Pages.EditProfile.class);
                Bundle bundle = new Bundle();

                bundle.putString("FirstName", FirstName.getText().toString());
                bundle.putString("LastName", LastName.getText().toString());
                bundle.putString("Username", UsernameText.getText().toString());
                bundle.putString("Bio", Description.getText().toString());
                bundle.putSerializable("Interests", ResponseInterests);
                bundle.putString("imageLink", ImageLink);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        FollowingNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeListOfEach(FollowingFollowersListView, FollowingList);
                Intent intent = new Intent(ProfilePage.this, FollowingFollowersPage.class);
                startActivity(intent);
            }
        });
        FollowersNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeListOfEach(FollowingFollowersListView, FollowersList);
                Intent intent = new Intent(ProfilePage.this, FollowingFollowersPage.class);
                startActivity(intent);
            }
        });


    }


    private void SetInformation(ProfileInformation response) throws IOException {
        ImageLink = response.getImageLink();
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);


        Glide.with(this).load(ImageLink).apply(options).skipMemoryCache(true) //2
                .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                .transform(new CircleCrop()).into(ProfilePicture);


        FirstName.setText(response.getFirstName());
        LastName.setText(response.getLastName());
        Description.setText(response.getBio());
        UsernameText.setText(response.getUsername());
        EmailText.setText(response.getEmail());

        // Interests
        ResponseInterests = response.getInterests();

        // Followers and Followings
        FollowersList = response.getFollowers();
        FollowingList = response.getFollowings();

        // Create a list view for following and followers
        FollowersNumber.setText(String.valueOf(FollowersList.size()));
        FollowingNumber.setText(String.valueOf(FollowingList.size()));


        // Interests
        ArrayList<ArrayList<Integer>> interests = new ArrayList<>(13);
        interests = response.getInterests();


        ArrayList<String> passToCreateLayout = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            List<Integer> indexes = interests.get(i);
            ArrayList<String> temp = new ArrayList<>();
            switch (i) {
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
            for (int j = 0; j < indexes.size(); j++) {

                passToCreateLayout.add(temp.get((int) (Math.log(indexes.get(j)) / Math.log(2))));
            }
        }
        CreateLayout(passToCreateLayout);

    }

    private Button CreateButtonInterest(String name, int id) {

        Button button = new Button(this);
        button.setText(name);
        button.setTextSize(14);
        button.setId(id);
        button.setGravity(Gravity.CENTER);
        RandomPick(button, id);
        return button;
    }

    private void CreateLayout(ArrayList<String> fields) {
        int size = fields.size();
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        for (int j = 0; j < size; j++) {
            Button btnTag = CreateButtonInterest(fields.get(j), j);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (400, 400);
            params.setMargins(2, 0, 10, 0);
            btnTag.setLayoutParams(params);
            row.addView(btnTag);
        }
        InterestContainer.addView(row);
    }

    private void MakeListOfEach(ListView layout, ArrayList<FollowingFollowers> fields) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.simple_list_1, R.id.textView, fields.toArray());
        layout.setAdapter(arrayAdapter);
    }

    private void RandomPick(Button button, int j) {
        String[] colors = new String[]{"#826263", "#9c7463", "#E98074", "#E85A4F", "#FF5722"};
        button.setBackgroundColor(Color.parseColor(colors[j % 5]));
    }
}