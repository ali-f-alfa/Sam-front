package com.example.chathouse.API;

import com.example.chathouse.ViewModels.Acount.InputSignupViewModel;
import com.example.chathouse.ViewModels.Acount.OutputLoginViewModel;
import com.example.chathouse.ViewModels.Acount.OutputSignupViewModel;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.UpdateProfileViewModel;
import com.example.chathouse.ViewModels.Search.InputSearchViewModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ChatHouseAPI {

    @POST("Account/Signup")
    Call<InputSignupViewModel> postSignup(@Body OutputSignupViewModel signupModel);

    @POST("Account/Login")
    Call<String> PostLogin(@Body OutputLoginViewModel Body);

    @POST("Account/Logout")
    Call<Void> PostLogout();

    @GET("Account/GetProfile")
    Call<ProfileInformation> GetProfile(@Query("username") String username);

    @POST("Account/UpdateProfile")
    Call<ProfileInformation> UpdateProfile(@Body UpdateProfileViewModel updateProfileViewModel);

    @GET("Search/Suggest")
    Call<List<InputSearchViewModel>> Suggest(@Query("PageSize") int PageSize, @Query("PageNumber") int PageNumber);

    @GET("Search/Category")
    Call<List<InputSearchViewModel>> Category(@Query("name") String name, @Query("category") Integer category, @Query("PageSize") int PageSize, @Query("PageNumber") int PageNumber);

    @GET("Search/Item")
    Call<List<InputSearchViewModel>> Item(@Query("name") String name, @Query("category") Integer category, @Query("item") int item, @Query("PageSize") int PageSize, @Query("PageNumber") int PageNumber);

    @Multipart
    @POST("Account/UpdateImage")
    Call<ProfileInformation> UpdateImage(@Part MultipartBody.Part image);

    @POST("Account/Follow")
    Call<ProfileInformation> FollowPost(@Query("username") String username);

    @POST("Account/UnFollow")
    Call<ProfileInformation> UnFollowPost(@Query("username") String username);
}
