package com.example.chathouse;

import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ChatHouseAPI {

    @POST("Account/Signup")
    Call<Object> postSignup(@Body OutputSignupViewModel signupModel);

    @POST("Account/Login")
    Call<Object> PostLogin(@Body OutputLoginViewModel Body);
}
