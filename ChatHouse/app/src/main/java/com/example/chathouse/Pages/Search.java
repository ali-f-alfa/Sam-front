package com.example.chathouse.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.ListView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    Button profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Locate the ListView in listview_main.xml
        TextView SearchError = findViewById(R.id.SearchError);
        list = (ListView) findViewById(R.id.SearchedPersonListView);
        editsearch = (SearchView) findViewById(R.id.search);
        profileBtn = (Button) findViewById(R.id.ProfBtn);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(Search.this, com.example.chathouse.Pages.ProfilePage.class);
                        Bundle bundle = new Bundle();

                        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
                        String Username = settings.getString("Username", "n/a");

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

        //fill suggested users
        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        String Token = settings.getString("Token", "n/a");

        ArrayList<SearchPerson> suggestedUsers = new ArrayList<SearchPerson>();

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
        ChatHouseAPI SuggestAPI = retrofit.create(ChatHouseAPI.class);

        Call<List<InputSearchViewModel>> Suggest = SuggestAPI.Suggest(10, 1);

        Suggest.enqueue(new Callback<List<InputSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "request was not successful ", Toast.LENGTH_LONG).show();
                }

                for (InputSearchViewModel person : response.body()) {
                    SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                    suggestedUsers.add(Person);
                }


                adapter = new ListViewAdapter(Search.this, suggestedUsers);
                list.setAdapter(adapter);

                if (suggestedUsers.size() == 0) {
                    TextView SearchError = findViewById(R.id.SearchError);
                    SearchError.setVisibility(View.VISIBLE);
                }
//                Toast.makeText(Search.this, "200 , and added to list", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Toast.makeText(Search.this, "onScrollStateChanged", Toast.LENGTH_SHORT).show();
                if (list.getLastVisiblePosition() == list.getAdapter().getCount() - 1 &&
                        list.getChildAt(list.getChildCount() - 1).getBottom() <= list.getHeight()) {


                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        // Locate the EditText in listview_main.xml
        editsearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        editsearch.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {


        TextView SearchError = findViewById(R.id.SearchError);
        SearchError.setVisibility(View.INVISIBLE);

        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        String Token = settings.getString("Token", "n/a");
        String Username = settings.getString("Username", "n/a");

        ArrayList<SearchPerson> CategoryUsers = new ArrayList<SearchPerson>();

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
        ChatHouseAPI CategoryAPI = retrofit.create(ChatHouseAPI.class);

        Call<List<InputSearchViewModel>> Category = CategoryAPI.Category(newText, null, 20, 1);

        Category.enqueue(new Callback<List<InputSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "not 200 ", Toast.LENGTH_LONG).show();
                    Toast.makeText(Search.this, response.toString(), Toast.LENGTH_LONG).show();
                }
                for (InputSearchViewModel person : response.body()) {
                    SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                    CategoryUsers.add(Person);
                }

                adapter = new ListViewAdapter(Search.this, CategoryUsers);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (CategoryUsers.size() == 0) {
                    TextView SearchError = findViewById(R.id.SearchError);
                    SearchError.setVisibility(View.VISIBLE);
                }
//                Toast.makeText(Search.this, "200 , and added to list", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });

//        adapter.UpdateView(SearchedPersons);

        return false;
    }

    public ArrayList<SearchPerson> GetSuggestedUsers() {
        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        String Token = settings.getString("Token", "n/a");
        String Username = settings.getString("Username", "n/a");

        ArrayList<SearchPerson> suggestedUsers = new ArrayList<SearchPerson>();

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
        ChatHouseAPI SuggestAPI = retrofit.create(ChatHouseAPI.class);

        Call<List<InputSearchViewModel>> Suggest = SuggestAPI.Suggest(20, 1);

        Suggest.enqueue(new Callback<List<InputSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "not 200 ", Toast.LENGTH_LONG).show();
                }

                for (InputSearchViewModel person : response.body()) {
                    SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                    suggestedUsers.add(Person);
                }
                Toast.makeText(Search.this, "200 , and added to list", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });
        return suggestedUsers;
    }

    public ArrayList<SearchPerson> GetCategoryUsers(String query) {
        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);
        String Token = settings.getString("Token", "n/a");
        String Username = settings.getString("Username", "n/a");

        ArrayList<SearchPerson> CategoryUsers = new ArrayList<SearchPerson>();

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
        ChatHouseAPI CategoryAPI = retrofit.create(ChatHouseAPI.class);

        Call<List<InputSearchViewModel>> Category = CategoryAPI.Category(query, null, 20, 1);

        Category.enqueue(new Callback<List<InputSearchViewModel>>() {
            @Override
            public void onResponse(Call<List<InputSearchViewModel>> call, Response<List<InputSearchViewModel>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Search.this, "not 200 ", Toast.LENGTH_LONG).show();
                    Toast.makeText(Search.this, response.toString(), Toast.LENGTH_LONG).show();
                }
                for (InputSearchViewModel person : response.body()) {
                    SearchPerson Person = new SearchPerson(person.getUsername(), person.getImagelink(), person.getFirstName(), person.getLastName());
                    CategoryUsers.add(Person);
                }
                Toast.makeText(Search.this, "200 , and added to list", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<List<InputSearchViewModel>> call, Throwable t) {
                Toast.makeText(Search.this, "Request failed", Toast.LENGTH_LONG).show();
            }
        });
        return CategoryUsers;
    }

}

class ListViewAdapter extends BaseAdapter {

    // Declare Variables

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

    public String getUsername(int position) {
        return SearchedPersonsList.get(position).getUserName();
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

    // Filter Class
    public void UpdateView(final ArrayList<SearchPerson> stationArrivalPOJO) {
        SearchedPersonsList = new ArrayList<>();
        SearchedPersonsList.addAll(stationArrivalPOJO);
        notifyDataSetChanged();
    }

}

class SearchPerson {
    private String UserName;
    private String ImageLink;
    private String FirstName;
    private String LastName;

//imageView.setImageBitmap(bmp);

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