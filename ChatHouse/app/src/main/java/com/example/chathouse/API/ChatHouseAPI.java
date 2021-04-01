package com.example.chathouse.API;

import com.example.chathouse.ViewModels.Acount.InputSignupViewModel;
import com.example.chathouse.ViewModels.Acount.OutputLoginViewModel;
import com.example.chathouse.ViewModels.Acount.OutputSignupViewModel;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatHouseAPI {

    @POST("Account/Signup")
    Call<InputSignupViewModel> postSignup(@Body OutputSignupViewModel signupModel);

    @POST("Account/Login")
    Call<String> PostLogin(@Body OutputLoginViewModel Body);

    @GET("Account/GetProfile")
    Call<ProfileInformation> GetProfile(@Query("username") String username);

}
