package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chathouse.R;
import com.example.chathouse.ViewModels.Acount.FollowingFollowers;
import com.example.chathouse.ViewModels.Acount.SearchPerson;

import java.util.ArrayList;

public class FollowingFollowersPage extends AppCompatActivity {
    private ListView FollowingFollowersListView;
    private TextView FollowerFollowing;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.gc();

        Intent intent = new Intent(FollowingFollowersPage.this, com.example.chathouse.Pages.ProfilePage.class);
        Bundle bundle = getIntent().getExtras();

        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        String UsernameText = settings.getString("Username", "n/a");

        if(UsernameText.equals(bundle.getString("User")))
        {
            bundle.putString("Username", UsernameText);
            intent.putExtras(bundle);
            startActivity(intent);

            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings  = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String themeName = settings.getString("ThemeName", "Theme");
        if (themeName.equalsIgnoreCase("DarkTheme")) {
            setTheme(R.style.DarkTheme_ChatHouse);
        } else if (themeName.equalsIgnoreCase("Theme")) {
            setTheme(R.style.Theme_ChatHouse);
        }
        setContentView(R.layout.activity_following_followers_page);
        FollowerFollowing = (TextView)findViewById(R.id.FollowingfollowersText);
        FollowingFollowersListView = (ListView) findViewById(R.id.FollowingFollowersListView);
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.FollowingBack);
        if (themeName.equalsIgnoreCase("DarkTheme")) {
            layout.setBackgroundResource(R.drawable.b22d);
        } else if (themeName.equalsIgnoreCase("Theme")) {
            layout.setBackgroundResource(R.drawable.b22);
        }
        Intent i = getIntent();
        Bundle bundle = getIntent().getExtras();
        String Title = bundle.getString("Name");
        System.out.println(Title);

        FollowerFollowing.setText(Title);

        ArrayList<FollowingFollowers> FollowingList = new ArrayList<FollowingFollowers>();
        ArrayList<SearchPerson> suggestedUsers = new ArrayList<SearchPerson>();

        FollowingList = (ArrayList<FollowingFollowers>)i.getSerializableExtra("Following");

        for (FollowingFollowers person : FollowingList) {
                SearchPerson Person = new SearchPerson(person.getUsername(), person.getImageLink(), person.getFirstName(), person.getLastName());
                suggestedUsers.add(Person);
            }

            ListViewAdapter adapter = new ListViewAdapter(FollowingFollowersPage.this, suggestedUsers);
            FollowingFollowersListView.setAdapter(adapter);

        FollowingFollowersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(FollowingFollowersPage.this, com.example.chathouse.Pages.ProfilePage.class);
                Bundle bundle = new Bundle();

                SearchPerson p = (SearchPerson) FollowingFollowersListView.getAdapter().getItem(position);
                String Username = p.getUsername();
                bundle.putString("Username", Username);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
}