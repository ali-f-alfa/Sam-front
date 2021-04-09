package com.example.chathouse.Pages;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.chathouse.API.ChatHouseAPI;
import com.example.chathouse.R;
import com.example.chathouse.Utility.Constants;
import com.example.chathouse.ViewModels.Acount.Interests;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.UpdateProfileViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private HorizontalScrollView Interests;
    public ArrayList<ArrayList<Integer>> SelectedInterests = new ArrayList<>(14);
    private TextView SetNewPicture;
    private HorizontalScrollView InterestEdit;
    private TextView textView;
    private ImageView ProfilePic;
    public RequestBody fbody;
    public MultipartBody.Part requestImage;
    private Uri picUri;
    private Boolean ImageChanged = false;
    private Boolean Ans1 = false;
    private Boolean Ans2 = false;
    private ChatHouseAPI API;

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
        Interests = (HorizontalScrollView)findViewById(R.id.Interests);
        SetNewPicture = (TextView)findViewById(R.id.SetNewPicture);
        textView = (TextView)findViewById(R.id.textView);
        InterestEdit = (HorizontalScrollView)findViewById((R.id.InterestEdit));
        ProfilePic = (ImageView)findViewById(R.id.ProfileImageEdit);





        Bundle bundle = getIntent().getExtras();
        SharedPreferences settings = getSharedPreferences("Storage", MODE_PRIVATE);


        String Token = settings.getString("Token", "n/a");
        String UsernameText = settings.getString("Username", "n/a");

        Bio.setText(bundle.getString("Bio"));
        Username.setText(UsernameText);
        FirstName.setText(bundle.getString("FirstName"));
        LastName.setText(bundle.getString("LastName"));

        Username.setEnabled(false);
        ArrayList<ArrayList<Integer>> interests = new ArrayList<>();
        interests = (ArrayList<ArrayList<Integer>>)bundle.getSerializable("Interests");

//        InitializeSelectedInterests(interests);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);



        Glide.with(this).load(bundle.getString("imageLink")).apply(options).skipMemoryCache(true) //2
                .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                .transform(new CircleCrop()).into(ProfilePic);


        for(int i = 0; i < 14; i++){
            SelectedInterests.add(new ArrayList<>());
        }

        SelectedInterests = interests;

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
        API = retrofit.create(ChatHouseAPI.class);


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
                                System.out.println(response.code() + response.errorBody().string());
                            } catch (IOException e) {
                                System.out.println(response.errorBody().toString());
                                e.printStackTrace();
                            }
                            return;
                        }
                        System.out.println("change Okay");
                        Intent intent = new Intent(EditProfile.this, com.example.chathouse.Pages.ProfilePage.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("Username", UsernameText);
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
                selectImage(v.getContext());

            }
        });



        MakeListOfEach(Wellness, com.example.chathouse.ViewModels.Acount.Interests.Wellness.getArrayString(), 0);
        MakeListOfEach(Identity, com.example.chathouse.ViewModels.Acount.Interests.Identity.getArrayString(), 1);
        MakeListOfEach(Places, com.example.chathouse.ViewModels.Acount.Interests.Places.getArrayString(), 2);
        MakeListOfEach(WorldAffairs, com.example.chathouse.ViewModels.Acount.Interests.WorldAffairs.getArrayString(), 3);
        MakeListOfEach(Tech, com.example.chathouse.ViewModels.Acount.Interests.Tech.getArrayString(), 4);
        MakeListOfEach(HangingOut, com.example.chathouse.ViewModels.Acount.Interests.HangingOut.getArrayString(), 5);
        MakeListOfEach(KnowLedge, com.example.chathouse.ViewModels.Acount.Interests.KnowLedge.getArrayString(), 6);
        MakeListOfEach(Hustle, com.example.chathouse.ViewModels.Acount.Interests.Hustle.getArrayString(), 7);
        MakeListOfEach(Sports, com.example.chathouse.ViewModels.Acount.Interests.Sports.getArrayString(), 8);
        MakeListOfEach(Arts, com.example.chathouse.ViewModels.Acount.Interests.Arts.getArrayString(), 9);
        MakeListOfEach(Life, com.example.chathouse.ViewModels.Acount.Interests.Life.getArrayString(), 10);
        MakeListOfEach(Languages, com.example.chathouse.ViewModels.Acount.Interests.Languages.getArrayString(), 11);
        MakeListOfEach(Entertainment, com.example.chathouse.ViewModels.Acount.Interests.Entertainment.getArrayString(), 12);
        MakeListOfEach(Faith, com.example.chathouse.ViewModels.Acount.Interests.Faith.getArrayString(), 13);



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            ImageChanged = true;
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round);



                        Glide.with(this).load("").apply(options).skipMemoryCache(true) //2
                                .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                                .transform(new CircleCrop()).into(ProfilePic);
//                        ProfilePic.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();

                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.mipmap.ic_launcher_round)
                                        .error(R.mipmap.ic_launcher_round);



                                Glide.with(this).load(picturePath).apply(options).skipMemoryCache(true) //2
                                        .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                                        .transform(new CircleCrop()).into(ProfilePic);
//                                ProfilePic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                File file = new File(picturePath);
                                fbody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                requestImage = MultipartBody.Part.createFormData("image", file.getName(), fbody);
                                Call<ProfileInformation> UpdateImage = API.UpdateImage(requestImage);
                                UpdateImage.enqueue(new Callback<ProfileInformation>() {
                                    @Override
                                    public void onResponse(Call<ProfileInformation> call, Response<ProfileInformation> response) {
                                        if (!response.isSuccessful()) {
                                            try {
                                                System.out.println(response.code() + response.errorBody().string());
                                            } catch (IOException e) {
                                                System.out.println(response.errorBody().toString());
                                                e.printStackTrace();
                                            }
                                            return;
                                        }
                                        Ans2 = true;
                                        System.out.println("Image Okay");

                                    }

                                    @Override
                                    public void onFailure(Call<ProfileInformation> call, Throwable t) {
                                        System.out.println("f" + t.getMessage());
                                    }
                                });

                                cursor.close();
                            }
                        }

                    }
                    break;

            }
        }

    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {

                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);

//                if (options[item].equals("Take Photo")) {
//                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(takePicture, 0);

//                }
                if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private UpdateProfileViewModel Edit(){
        String first = FirstName.getText().toString();
        String last = LastName.getText().toString();
        String bio = Bio.getText().toString();
        UpdateProfileViewModel post = new UpdateProfileViewModel(null, first, last, bio, SelectedInterests);

        return post;
    }

    private void MakeListOfEach(ListView layout, ArrayList<String> fields, int cat){
        ListViewAdapter arrayAdapter = new ListViewAdapter(this, fields, cat);
        layout.setAdapter(arrayAdapter);
    }

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };
    private static final int REQUEST_PERMISSIONS = 12345;
    private void InitializeSelectedInterests(ArrayList<ArrayList<Integer>> interests) {
        for(int i = 0; i < 14; i++){
            List<Integer> indexes = interests.get(i);

            for(int j = 0; j < indexes.size(); j++){
                switch (i){
                    case 0:
                        Wellness.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Wellness.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 1:
                        Identity.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Identity.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 2:
                        Places.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Places.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 3:
                        WorldAffairs.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        WorldAffairs.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 4:
                        Tech.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Tech.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 5:
                        HangingOut.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        HangingOut.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 6:
                        KnowLedge.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        KnowLedge.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 7:
                        Hustle.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Hustle.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 8:
                        Sports.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Sports.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 9:
                        Arts.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Arts.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 10:
                        Life.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Life.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 11:
                        Languages.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Languages.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 12:
                        Entertainment.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Entertainment.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                    case 13:
                        Faith.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setSelected(true);
                        Faith.getAdapter().getView((int)(Math.log(indexes.get(j)) / Math.log(2)), Wellness, null).setBackgroundColor(Color.GRAY);
                        break;
                }

            }
        }
    }

}


class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<String> Items = null;
    private ArrayList<String> arraylist;
    private int category;

    public ListViewAdapter(Context context, List<String> items, int cat) {
        mContext = context;
        this.Items = items;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(Items);
        this.category = cat;
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public String getItem(int position) {
        return Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        holder = new ViewHolder();
        view = inflater.inflate(R.layout.simple_list_1, null);
        // Locate the TextViews in listview_item.xml
        holder.name = (TextView) view.findViewById(R.id.textView);
        view.setTag(holder);

        holder.name.setText(Items.get(position));


        return holder.name;
    }


}

