package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.ListView;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.HorizontalScrollView;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Search.InputSearchViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

public class Search extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // Declare Variables
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    ArrayList<SearchPerson> SearchedPersons = new ArrayList<SearchPerson>();
    TextView SearchError;
    TextView SearchTitle;
    Button profileBtn;
    int i = 3;
    SharedPreferences settings;
    String Token;
    String Username;
    ChatHouseAPI SearchAPI;
    int mode;
    int selected_category;
    int selected_item;
    boolean endOfList;
    GridLayout category_grid;
    ScrollView category_scroll;
    GridLayout item_grid;
    HorizontalScrollView item_scroll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mode = 0; // 0 => suggest  ,  1 => category  , 2 => item
        selected_category = 0;
        selected_item = 0;
        endOfList = false;
        SearchError = findViewById(R.id.SearchError);
        SearchTitle = findViewById(R.id.SearchTitle);
        list = (ListView) findViewById(R.id.SearchedPersonListView);
        editsearch = (SearchView) findViewById(R.id.search);
        profileBtn = (Button) findViewById(R.id.ProfBtn);
        settings = getSharedPreferences("Storage", MODE_PRIVATE);
        Token = settings.getString("Token", "n/a");
        Username = settings.getString("Username", "n/a");
        category_grid = (GridLayout) findViewById(R.id.category_grid);
        category_scroll = (ScrollView) findViewById(R.id.Category);
        item_grid = (GridLayout) findViewById(R.id.item_grid);
        item_scroll = (HorizontalScrollView) findViewById(R.id.Items);

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
        SearchAPI = retrofit.create(ChatHouseAPI.class);

        int childCount = category_grid.getChildCount();

        for (int i= 0; i < childCount; i++){
            int n = i;
            CardView container = (CardView) category_grid.getChildAt(i);
            container.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    selected_category = n;
                    mode = 1;

                    SearchedPersons.clear();
                    SearchError.setVisibility(View.INVISIBLE);
                    TextView t = (TextView) container.getChildAt(0);
                    SearchTitle.setText("Search in "+ t.getText());


                    Call<List<InputSearchViewModel>> Req = SearchAPI.Category(editsearch.getQuery().toString(), selected_category, 10, 1);
                    Req.enqueue(new Callback<List<InputSearchViewModel>>() {
                        @Override
                        public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(Search.this, "unsuccessful", Toast.LENGTH_LONG).show();
//                                Toast.makeText(Search.this, response.toString(), Toast.LENGTH_LONG).show();
                            }
                            for (InputSearchViewModel person : response.body()) {
                                SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                                SearchedPersons.add(Person);
                            }

                            adapter = new ListViewAdapter(Search.this, SearchedPersons);
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if (SearchedPersons.size() == 0) {
                                SearchError.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                            Toast.makeText(Search.this, "Request failed", Toast.LENGTH_LONG).show();
                        }
                    });

                    category_scroll.setVisibility(View.INVISIBLE);
                    CreateItems(n,item_grid);
                }
            });
        }

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(Search.this, com.example.chathouse.Pages.ProfilePage.class);
                        Bundle bundle = new Bundle();


                        bundle.putString("Username", Username);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Search.this, com.example.chathouse.Pages.ProfilePage.class);
                Bundle bundle = new Bundle();

                SearchPerson p = (SearchPerson) list.getAdapter().getItem(position);
                String Username = p.getUserName();
                bundle.putString("Username", Username);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        Call<List<InputSearchViewModel>> Suggest = SearchAPI.Suggest(10, 1);
        Suggest.enqueue(new Callback<List<InputSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "request was not successful ", Toast.LENGTH_LONG).show();
                }

                for (InputSearchViewModel person : response.body()) {
                    SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                    SearchedPersons.add(Person);
                }

                adapter = new ListViewAdapter(Search.this, SearchedPersons);
                list.setAdapter(adapter);

                if (SearchedPersons.size() == 0) {
                    SearchError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });

        //pagination
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (!endOfList) {
                    if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 &&
                            list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) { //end of scroll

                        Toast.makeText(Search.this, "loading...", Toast.LENGTH_SHORT).show();


                        Call<List<InputSearchViewModel>> Req;
                        if (mode == 0){
                            String xxx = editsearch.getQuery().toString();
                            Req = SearchAPI.Category(editsearch.getQuery().toString(), null, 5, i++);
                        }
                        else if (mode == 1)
                            Req = SearchAPI.Category(editsearch.getQuery().toString(), selected_category, 5, i++);
                        else
                            Req = SearchAPI.Item(editsearch.getQuery().toString(), selected_category, selected_item, 5, i++);

                        Req.enqueue(new Callback<List<InputSearchViewModel>>() {
                            @Override
                            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(Search.this, "request was not successful ", Toast.LENGTH_LONG).show();
                                }

                                if (response.body().size() == 0)
                                    endOfList = true;
                                else
                                    endOfList = false;

                                for (InputSearchViewModel person : response.body()) {
                                    SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                                    SearchedPersons.add(Person);
                                }

                                adapter.notifyDataSetChanged();


                                if (SearchedPersons.size() == 0) {
                                    SearchError.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                                Toast.makeText(Search.this, "Request failed", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        editsearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        editsearch.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        SearchError.setVisibility(View.INVISIBLE);

        SearchedPersons.clear();
        adapter.notifyDataSetChanged();
        endOfList = false;
        Call<List<InputSearchViewModel>> Req;
        if (mode == 0)
            Req = SearchAPI.Category(newText, null, 10, 1);
        else if (mode == 1)
            Req = SearchAPI.Category(editsearch.getQuery().toString(), selected_category, 10, 1);
        else
            Req = SearchAPI.Item(editsearch.getQuery().toString(), selected_category, selected_item, 10, 1);



        Req.enqueue(new Callback<List<InputSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "not 200 ", Toast.LENGTH_LONG).show();
                    Toast.makeText(Search.this, response.toString(), Toast.LENGTH_LONG).show();
                }
                SearchedPersons.clear();
                adapter.notifyDataSetChanged();
                for (InputSearchViewModel person : response.body()) {
                    SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                    SearchedPersons.add(Person);
                }

                adapter = new ListViewAdapter(Search.this, SearchedPersons);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (SearchedPersons.size() == 0) {
                    SearchError.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });

        return false;
    }



    public void CreateItems(int n, GridLayout layout){
        ArrayList<String> temp = new ArrayList<>();
        switch (n){
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
            String t = temp.get(x);
            AddItem(temp.get(x) , item_grid).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_item = (int)Math.pow(2, xx);
                    mode = 2;

                    SearchedPersons.clear();
                    SearchError.setVisibility(View.INVISIBLE);

                    Call<List<InputSearchViewModel>> Req = SearchAPI.Item(editsearch.getQuery().toString(), selected_category, selected_item, 10, 1);
                    Req.enqueue(new Callback<List<InputSearchViewModel>>() {
                        @Override
                        public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(Search.this, "unsuccessful", Toast.LENGTH_LONG).show();
//                                Toast.makeText(Search.this, response.toString(), Toast.LENGTH_LONG).show();
                            }
                            for (InputSearchViewModel person : response.body()) {
                                SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                                SearchedPersons.add(Person);
                            }

                            adapter = new ListViewAdapter(Search.this, SearchedPersons);
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if (SearchedPersons.size() == 0) {
                                SearchError.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                            Toast.makeText(Search.this, "Request failed", Toast.LENGTH_LONG).show();
                        }
                    });
                    item_scroll.setVisibility(View.INVISIBLE);
                    SearchTitle.setText("Search in "+ t);


                }
            });
        }
    }

    public CardView AddItem(String name, GridLayout layout){

        CardView cardview = new CardView(this);

        LayoutParams layoutparams = new LayoutParams(
                450,
                LayoutParams.WRAP_CONTENT
        );
        layoutparams.setMargins(20,35,20,35   );

        cardview.setLayoutParams(layoutparams);

        cardview.setRadius(50);

        cardview.setPadding(25, 25, 25, 25);

        cardview.setCardBackgroundColor(Color.WHITE);


        cardview.setMaxCardElevation(6);


        TextView textview = new TextView(this);

        LayoutParams tlayoutparams = new LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT
        );
        tlayoutparams.setMargins(5,15,5,15   );

        textview.setLayoutParams(tlayoutparams);

        textview.setText(name);

        textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

//        textview.setTextColor(Color.WHITE);

        textview.setPadding(8,8,8,8);

        textview.setGravity(Gravity.CENTER);

        cardview.addView(textview);

        layout.addView(cardview);
        return cardview;
    }
//    public CardView AddCategory(String name, GridLayout layout){
//
//        CardView cardview = new CardView(this);
//
//        LayoutParams layoutparams = new LayoutParams(
//                LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT
//        );
//        layoutparams.setMargins(12,12,12,12);
//        cardview.setLayoutParams(layoutparams);
//
//        cardview.setRadius(15);
//
//        cardview.setPadding(25, 25, 25, 25);
//
//        cardview.setCardBackgroundColor(Color.MAGENTA);
//
//        cardview.setCardElevation(6);
//        cardview.setcorner;
//
//        TextView textview = new TextView(this);
//
//        textview.setLayoutParams(layoutparams);
//
//        textview.setText("CardView Programmatically");
//
//        textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
//
//        textview.setTextColor(Color.WHITE);
//
//        textview.setPadding(25,25,25,25);
//
//        textview.setGravity(Gravity.CENTER);
//
//        cardview.addView(textview);
//
//        layout.addView(cardview);
//
//    }

}

class ListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<SearchPerson> SearchedPersonsList = null;
    private ArrayList<SearchPerson> arraylist;

    public ListViewAdapter(Context context, List<SearchPerson> SearchedPersonsList) {
        mContext = context;
        this.SearchedPersonsList = SearchedPersonsList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<SearchPerson>();
        this.arraylist.addAll(SearchedPersonsList);

    }

    public class ViewHolder {
        TextView userName;
        TextView name;
        ImageView Image;

    }

    @Override
    public int getCount() {
        return SearchedPersonsList.size();
    }

    @Override
    public SearchPerson getItem(int position) {
        return SearchedPersonsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        holder = new ViewHolder();
        view = inflater.inflate(R.layout.list_view_searched_items, null);
        // Locate the TextViews in listview_item.xml
        holder.userName = (TextView) view.findViewById(R.id.itemUsername);
        holder.name = (TextView) view.findViewById(R.id.itemFirstNameAndLastName);
        holder.Image = (ImageView) view.findViewById(R.id.itemImage);
        view.setTag(holder);

        // Set the results into TextViews
        if (SearchedPersonsList.get(position).getFirstName() != null && SearchedPersonsList.get(position).getLastName() != null)
            holder.name.setText(SearchedPersonsList.get(position).getFirstName() + " " + SearchedPersonsList.get(position).getLastName());

        holder.userName.setText(SearchedPersonsList.get(position).getUserName());


        if (SearchedPersonsList.get(position).getImageLink() != null)
            Glide.with(mContext).load(SearchedPersonsList.get(position).getImageLink()).into(holder.Image);

        return view;
    }
}

class SearchPerson {
    private String UserName;
    private String ImageLink;
    private String FirstName;
    private String LastName;


    public SearchPerson(String userName, String ImageLink, String firstName, String lastName) {
        this.UserName = userName;
        FirstName = firstName;
        LastName = lastName;
        this.ImageLink = ImageLink;
    }

    public String getUserName() {
        return UserName;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }
}