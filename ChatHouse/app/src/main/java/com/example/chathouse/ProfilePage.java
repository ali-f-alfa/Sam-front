package com.example.chathouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
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
    private TextView Name;
    private TextView UserName;
    private TextView Description;
    private TextView FollowersNumber;
    private TextView FollowingNumber;
    private TextView Followers;
    private TextView Following;
    private Button Message;
    private Button Follow;
    private ImageView ProfilePicture;
    private TextView Memberof;
    private Button EditProfile;
    private LinearLayout InterestContainer;
    private TextView UsernameText;
    private TextView EmailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        UserName = (EditText)findViewById(R.id.Username);
        ProfilePicture = (ImageView) findViewById(R.id.ProfileImage);
        Name = (TextView)findViewById(R.id.Name);
        Description = (TextView)findViewById(R.id.Bio);
        FollowersNumber = (TextView)findViewById(R.id.Followers);
        FollowingNumber = (TextView)findViewById(R.id.Following);
        Followers = (TextView)findViewById(R.id.FollowersText);
        Following = (TextView)findViewById(R.id.FollowingText);
        Message = (Button)findViewById(R.id.MessageButton);
        Follow = (Button)findViewById(R.id.FollowButton);
        Memberof = (TextView)findViewById(R.id.MemberOf);
        UsernameText = (TextView)findViewById(R.id.UsernameText);
        EmailText = (TextView)findViewById(R.id.EmailText);
        EditProfile = (Button)findViewById(R.id.EditProfileButton);
        InterestContainer = (LinearLayout)findViewById(R.id.ContainerButton);

        Memberof.setVisibility(View.INVISIBLE);


        Bundle bundle = getIntent().getExtras();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", bundle.getString("Token"))
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUrl.get("http://10.0.2.2:13524/api/"))
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory((GsonConverterFactory.create(gson)))
                .build();
        ChatHouseAPI GetProfileAPI = retrofit.create(ChatHouseAPI.class);

        Call<ProfileInformation> GetProfile = GetProfileAPI.GetProfile(bundle.getString("Username"));

        GetProfile.enqueue(new Callback<ProfileInformation>() {
            @Override
            public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                if(!response.isSuccessful()){
                    try {
                        Name.setText(response.errorBody().string());
                    } catch (IOException e) {
                        Name.setText(response.errorBody().toString());
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

                SetInformation(Response);

            }

            @Override
            public void onFailure(Call<ProfileInformation> call, Throwable t) {
                Name.setText(t.getMessage().toString());
                Toast.makeText(ProfilePage.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });

        EditProfile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, EditProfile.class);

                startActivity(intent);
            }
        });



    }

    private void SetInformation(ProfileInformation response){
        Name.setText(response.getFirstName() + response.getLastName());
        Description.setText(response.getBio());
        UsernameText.setText(response.getUsername());
        EmailText.setText(response.getEmail());

        // Interests

        // Followers and Followings
        List<FollowingFollowers> Fr = response.getFollowers();
        List<FollowingFollowers> Fi = response.getFollowings();
//        Followers.setText(Fr.size());
//        Following.setText(Fi.size());
    }
    private void CreateButtonInterest(LinearLayout linearLayout, String name){
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        Button button = new Button(this);
        button.setText(name);
        button.setTextSize(10);
        button.setGravity(Gravity.CENTER);
        button.setLayoutParams(params);

        linearLayout.addView(button);
    }
}