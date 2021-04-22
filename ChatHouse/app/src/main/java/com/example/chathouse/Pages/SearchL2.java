package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SearchView;

import com.example.chathouse.R;

public class SearchL2 extends AppCompatActivity implements SearchView.OnQueryTextListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_l2);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}