package com.example.chathouse.Pages;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.Interests;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.UpdateProfileViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
    public ArrayList<ArrayList<Integer>> SelectedInterests = new ArrayList<>();
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
        FirstName.setText(bundle.getString("FirstName"));
        LastName.setText(bundle.getString("LastName"));

        ArrayList<ArrayList<Integer>> interests = new ArrayList<>();
        interests = (ArrayList<ArrayList<Integer>>)bundle.getSerializable("Interests");

//        InitializeSelectedInterests(interests);
        SelectedInterests = interests;
        System.out.println(SelectedInterests);
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
        ChatHouseAPI API = retrofit.create(ChatHouseAPI.class);


        Call<Void> Logout = API.PostLogout();

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ProfileInformation> UpdateProfile = API.UpdateProfile(Edit());

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
                        System.out.println(response.body().getInterests());
                        System.out.println(SelectedInterests);

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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(0).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(0).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(0).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
                System.out.println(SelectedInterests);

            }
        });
        Identity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(1).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(1).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(1).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }

            }
        });
        Places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(2).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(2).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(2).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        WorldAffairs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(3).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(3).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(3).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        Tech.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(4).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(4).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(4).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        HangingOut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(5).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(5).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(5).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        KnowLedge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(6).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(6).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(6).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        Hustle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(7).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(7).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(7).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        Sports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(8).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(8).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(8).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        Arts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(9).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(9).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(9).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        Life.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(10).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(10).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(10).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        Languages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(11).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(11).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(11).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        Entertainment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(12).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(12).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(12).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });
        Faith.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(SelectedInterests.get(13).contains((int)Math.pow(2, id))){
                    Iterator itr = SelectedInterests.get(13).iterator();
                    while (itr.hasNext())
                    {
                        int data = (Integer)itr.next();
                        if (data == Math.pow(2, id))
                            itr.remove();
                    }
                    view.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                }
                else{
                    SelectedInterests.get(13).add((int) (Math.pow(2, id)));
                    view.setBackgroundColor(Color.GRAY);
                    view.setSelected(true);
                }
            }
        });



    }

    private void InitializeSelectedInterests(ArrayList<ArrayList<Integer>> interests) {
        SelectedInterests = interests;
        for(int i = 0; i < 14; i++){
            List<Integer> indexes = interests.get(i);

            for(int j = 0; j < indexes.size(); j++){
                switch (i){
                    case 0:
                        Wellness.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), InterestEdit, null).setSelected(true);
                        Wellness.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), InterestEdit, null).setBackgroundColor(Color.GRAY);
                        break;
//                    case 1:
//                        Identity.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Identity.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 2:
//                        Places.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Places.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 3:
//                        WorldAffairs.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        WorldAffairs.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 4:
//                        Tech.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Tech.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 5:
//                        HangingOut.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        HangingOut.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 6:
//                        KnowLedge.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        KnowLedge.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 7:
//                        Hustle.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Hustle.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 8:
//                        Sports.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Sports.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 9:
//                        Arts.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Arts.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 10:
//                        Life.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Life.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 11:
//                        Languages.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Languages.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 12:
//                        Entertainment.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Entertainment.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
//                    case 13:
//                        Faith.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setSelected(true);
//                        Faith.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), null, null).setBackgroundColor(Color.GRAY);
//                        break;
                }

            }
        }
    }


    private UpdateProfileViewModel Edit(){
        String first = FirstName.getText().toString();
        String last = LastName.getText().toString();
        String bio = Bio.getText().toString();
        UpdateProfileViewModel post = new UpdateProfileViewModel(null, first, last, bio, SelectedInterests);

        return post;
    }

    private void MakeListOfEach(ListView layout, ArrayList<String> fields){
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.simple_list_1, R.id.textView, fields.toArray());
        layout.setAdapter(arrayAdapter);
    }


}