package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.FollowingFollowers;
import com.example.chathouse.ViewModels.Acount.Interests;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        UserName = (EditText)findViewById(R.id.Username);
        ProfilePicture = (ImageView) findViewById(R.id.ProfileImage);
        LastName = (TextView)findViewById(R.id.LastName);
        FirstName = (TextView)findViewById(R.id.FirstName);
        Description = (TextView)findViewById(R.id.Bio);
        FollowersNumber = (TextView)findViewById(R.id.Followers);
        FollowingNumber = (TextView)findViewById(R.id.Following);
        Followers = (TextView)findViewById(R.id.FollowersText);
        Following = (TextView)findViewById(R.id.FollowingText);
        Message = (Button)findViewById(R.id.MessageButton);
        Follow = (Button)findViewById(R.id.FollowButton);
//        Memberof = (TextView)findViewById(R.id.MemberOf);
        UsernameText = (TextView)findViewById(R.id.UsernameText);
        EmailText = (TextView)findViewById(R.id.EmailText);
        EditProfile = (Button)findViewById(R.id.EditProfileButton);
        InterestContainer = (LinearLayout)findViewById(R.id.ContainerButton);
        Interests = (HorizontalScrollView)findViewById(R.id.Interests);

//        Memberof.setVisibility(View.INVISIBLE);

        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);


        String Token = settings.getString("Token", "n/a");
        String Username = settings.getString("Username", "n/a");

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
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

        Call<ProfileInformation> GetProfile = GetProfileAPI.GetProfile(Username);

        GetProfile.enqueue(new Callback<ProfileInformation>() {
            @Override
            public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                if(!response.isSuccessful()){
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
                if(Response.getMe()){
                    Message.setVisibility(View.INVISIBLE);
                    Follow.setVisibility(View.INVISIBLE);
                    EditProfile.setVisibility(View.VISIBLE);
                }
                else {
                    if(FollowingList.contains(Response)){
                        Follow.setText("Unfollow");
                    }
                }

                SetInformation(Response);
                System.out.println(Response.getInterests());


            }

            @Override
            public void onFailure(Call<ProfileInformation> call, Throwable t) {
                FirstName.setText(t.getMessage().toString());
                Toast.makeText(ProfilePage.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });

        EditProfile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, com.example.chathouse.Pages.EditProfile.class);
                Bundle bundle = new Bundle();

                bundle.putString("FirstName", FirstName.getText().toString());
                bundle.putString("LastName", LastName.getText().toString());
                bundle.putString("Username", UsernameText.getText().toString());
                bundle.putString("Bio", Description.getText().toString());
                bundle.putSerializable("Interests", ResponseInterests);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }


    private void SetInformation(ProfileInformation response){
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
        ArrayList<ArrayList<Integer>> interests= new ArrayList<>(13);
        interests = response.getInterests();


        ArrayList<String> passToCreateLayout = new ArrayList<>();
        for(int i = 0; i < 14; i++){
            List<Integer> indexes = interests.get(i);
            ArrayList<String> temp = new ArrayList<>();
            switch (i){
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
            for(int j = 0; j < indexes.size(); j++){

                passToCreateLayout.add(temp.get((int)(Math.log(indexes.get(j)) / Math.log(2))));
            }
        }
        CreateLayout(passToCreateLayout);

    }
    private Button CreateButtonInterest(String name, int id){

        Button button = new Button(this);
        button.setText(name);
        button.setTextSize(10);
        button.setId(id);
        button.setGravity(Gravity.CENTER);

        return button;
    }

    private void CreateLayout(ArrayList<String> fields){
        System.out.println(fields);
        int size = fields.size();
        for (int i = 0; i < size / 4; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < 4; j++) {
                Button btnTag = CreateButtonInterest(fields.get(i * 4 + j), (i * 4) + j);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                row.addView(btnTag);
            }
            InterestContainer.addView(row);
        }
    }
}