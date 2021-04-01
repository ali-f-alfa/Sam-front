package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chathouse.R;

public class EditProfile extends AppCompatActivity {
    private Button Save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Save =  (Button)findViewById(R.id.SaveButton);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Send update request


                Intent intent = new Intent(EditProfile.this, ProfilePage.class);
                startActivity(intent);
            }
        });



    }
}