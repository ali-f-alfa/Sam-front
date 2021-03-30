package com.example.chathouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        UserName = (EditText)findViewById(R.id.Username);
        ProfilePicture = (ImageView) findViewById(R.id.ProfileImage);
        Name = (TextView)findViewById(R.id.Name);
//        Name = (TextView)findViewById(R.id.Name);
        Description = (TextView)findViewById(R.id.Bio);
        FollowersNumber = (TextView)findViewById(R.id.Followers);
        FollowingNumber = (TextView)findViewById(R.id.Following);
        Followers = (TextView)findViewById(R.id.FollowersText);
        Following = (TextView)findViewById(R.id.FollowingText);
        Message = (Button)findViewById(R.id.MessageButton);
        Follow = (Button)findViewById(R.id.FollowButton);
        Memberof = (TextView)findViewById(R.id.MemberOf);

        Memberof.setVisibility(View.INVISIBLE);


        Bundle bundle = getIntent().getExtras();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + bundle.getString("Token"))
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

        Call<ProfileInformation> GetProfile = GetProfileAPI.GetProfile(bundle.getString("Username"), "Bearer " + bundle.getString("Token"));

        GetProfile.enqueue(new Callback<ProfileInformation>() {
            @Override
            public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                if(!response.isSuccessful()){
                    try {
                        Name.setText(response.message());
                        Description.setText(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Name.setText(e.getMessage());
                    }
                    return;
                }

                // Set the values from back
                ProfileInformation Response = response.body();
                Description.setText(Response.getUsername());

            }

            @Override
            public void onFailure(Call<ProfileInformation> call, Throwable t) {
                Toast.makeText(ProfilePage.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });

    }
}