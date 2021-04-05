package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.ViewModels.Acount.Interests;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.UpdateProfileViewModel;
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

public class EditProfile extends AppCompatActivity {
    private Button Save;
    private Button LogoutButton;
    private ListView Wellness;
    private ListView Identity;
    private ListView Places;
    private ListView WorldAffairs;
    private ListView Tech;
    private ListView HangingOut;
    private ListView KnowLedge;
    private ListView Hustle;
    private ListView Sports;
    private ListView Arts;
    private ListView Life;
    private ListView Languages;
    private ListView Entertainment;
    private ListView Faith;
    private EditText Bio;
    private EditText Username;
    private EditText FirstName;
    private EditText LastName;
    private Switch OnOff;
    private HorizontalScrollView Interests;
    public ArrayList<ArrayList<Integer>> SelectedInterests = new ArrayList<>(13);
    private TextView SetNewPicture;
    private HorizontalScrollView InterestEdit;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Save =  (Button)findViewById(R.id.SaveButton);
        LogoutButton =  (Button)findViewById(R.id.LogoutButton);
        Wellness = (ListView)findViewById(R.id.Wellness);
        Identity = (ListView)findViewById(R.id.Identity);
        Places = (ListView)findViewById(R.id.Places);
        WorldAffairs = (ListView)findViewById(R.id.WorldAffaris);
        Tech = (ListView)findViewById(R.id.Tech);
        HangingOut = (ListView)findViewById(R.id.HangingOut);
        KnowLedge = (ListView)findViewById(R.id.Knowledge);
        Hustle = (ListView)findViewById(R.id.Hustle);
        Sports = (ListView)findViewById(R.id.Sports);
        Arts = (ListView)findViewById(R.id.Arts);
        Life = (ListView)findViewById(R.id.Life);
        Languages = (ListView)findViewById(R.id.Languages);
        Entertainment = (ListView)findViewById(R.id.Entertainment);
        Faith = (ListView)findViewById(R.id.Faith);
        Bio = (EditText)findViewById(R.id.BioEdit);
        Username = (EditText)findViewById(R.id.UsernameEdit);
        FirstName = (EditText)findViewById(R.id.FirstNameEdit);
        LastName = (EditText)findViewById(R.id.LastNameEdit);
        OnOff = (Switch)findViewById(R.id.InterestsOnOff);
        Interests = (HorizontalScrollView)findViewById(R.id.Interests);
        SetNewPicture = (TextView)findViewById(R.id.SetNewPicture);
        textView = (TextView)findViewById(R.id.textView);
        InterestEdit = (HorizontalScrollView)findViewById((R.id.InterestEdit));



        OnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Interests.setVisibility(View.INVISIBLE);
                }
                else{
                    Interests.setVisibility(View.VISIBLE);
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);


        String Token = settings.getString("Token", "n/a");
        String UsernameText = settings.getString("Username", "n/a");

        Bio.setText(bundle.getString("Bio"));
        Username.setText(UsernameText);
        String first = "";
        String last = "";
        if(bundle.getString("FirstName") != null){
            String[] Name = bundle.getString("FirstName").split(" ");
            for(int i = 1; i < Name.length; i++){
                last += Name[i];
            }
            first = Name[0];
        }


        FirstName.setText(first);
        LastName.setText(last);


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
                .baseUrl(HttpUrl.get("http://10.0.2.2:13524/api/"))
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory((GsonConverterFactory.create(gson)))
                .build();
        ChatHouseAPI API = retrofit.create(ChatHouseAPI.class);

        Call<ProfileInformation> UpdateProfile = API.UpdateProfile(Edit());
        Call<Void> Logout = API.PostLogout();

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Send update request
                UpdateProfile.enqueue(new Callback<ProfileInformation>() {
                    @Override
                    public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                        if(!response.isSuccessful()){
                            try {
                                SetNewPicture.setText("############" + response.errorBody().string());
                                System.out.println("############" + response.code());
                            } catch (IOException e) {
                                SetNewPicture.setText("$$$$$$$$$$$$$$" + response.errorBody().toString());
                                System.out.println("$$$$$$$$$$$$$$" + response.errorBody().toString());
                                e.printStackTrace();
                            }
                            return;
                        }
                        Intent intent = new Intent(EditProfile.this, com.example.chathouse.Pages.ProfilePage.class);

                        startActivity(intent);

                    }

                    @Override
                    public void onFailure(Call<ProfileInformation> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });
            }
        });

        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout Request
                Logout.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(!response.isSuccessful()){
                            try {
                                SetNewPicture.setText(response.errorBody().string());
                            } catch (IOException e) {
                                SetNewPicture.setText(response.errorBody().toString());
                                e.printStackTrace();
                            }
                            return;
                        }
                        Intent intent = new Intent(EditProfile.this, Login.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(EditProfile.this, "Request failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        SetNewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update image

            }
        });



        MakeListOfEach(Wellness, com.example.chathouse.ViewModels.Acount.Interests.Wellness.getArrayString());
        MakeListOfEach(Identity, com.example.chathouse.ViewModels.Acount.Interests.Identity.getArrayString());
        MakeListOfEach(Places, com.example.chathouse.ViewModels.Acount.Interests.Places.getArrayString());
        MakeListOfEach(WorldAffairs, com.example.chathouse.ViewModels.Acount.Interests.WorldAffairs.getArrayString());
        MakeListOfEach(Tech, com.example.chathouse.ViewModels.Acount.Interests.Tech.getArrayString());
        MakeListOfEach(HangingOut, com.example.chathouse.ViewModels.Acount.Interests.HangingOut.getArrayString());
        MakeListOfEach(KnowLedge, com.example.chathouse.ViewModels.Acount.Interests.KnowLedge.getArrayString());
        MakeListOfEach(Hustle, com.example.chathouse.ViewModels.Acount.Interests.Hustle.getArrayString());
        MakeListOfEach(Sports, com.example.chathouse.ViewModels.Acount.Interests.Sports.getArrayString());
        MakeListOfEach(Arts, com.example.chathouse.ViewModels.Acount.Interests.Arts.getArrayString());
        MakeListOfEach(Life, com.example.chathouse.ViewModels.Acount.Interests.Life.getArrayString());
        MakeListOfEach(Languages, com.example.chathouse.ViewModels.Acount.Interests.Languages.getArrayString());
        MakeListOfEach(Entertainment, com.example.chathouse.ViewModels.Acount.Interests.Entertainment.getArrayString());
        MakeListOfEach(Faith, com.example.chathouse.ViewModels.Acount.Interests.Faith.getArrayString());

        for(int i = 0; i < 14; i++){
            SelectedInterests.add(new ArrayList());
        }

        Wellness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(0).add(position);

            }
        });
        Identity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(1).add(position);
            }
        });
        Places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(2).add(position);
            }
        });
        WorldAffairs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(3).add(position);
            }
        });
        Tech.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(4).add(position);
            }
        });
        HangingOut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(5).add(position);
            }
        });
        KnowLedge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(6).add(position);
            }
        });
        Hustle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(7).add(position);
            }
        });
        Sports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(8).add(position);
            }
        });
        Arts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(9).add(position);
            }
        });
        Life.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(10).add(position);
            }
        });
        Languages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(11).add(position);
            }
        });
        Entertainment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(12).add(position);
            }
        });
        Faith.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedInterests.get(13).add(position);
            }
        });



    }



    private UpdateProfileViewModel Edit(){
        UpdateProfileViewModel post = new UpdateProfileViewModel(null, FirstName.getText().toString(), LastName.getText().toString(), Bio.getText().toString(), SelectedInterests);

        return post;
    }

    private void MakeListOfEach(ListView layout, ArrayList<String> fields){
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.simple_list_1, R.id.textView, fields.toArray());
        layout.setAdapter(arrayAdapter);
    }


}