package com.example.chathouse.API;

import com.example.chathouse.ViewModels.Acount.InputSignupViewModel;
import com.example.chathouse.ViewModels.Acount.OutputLoginViewModel;
import com.example.chathouse.ViewModels.Acount.OutputSignupViewModel;
import com.example.chathouse.ViewModels.Acount.ProfileInformation;
import com.example.chathouse.ViewModels.Acount.UpdateProfileViewModel;

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

    @Multipart
    @POST("Account/UpdateImage")
    Call<ProfileInformation> UpdateImage(@Part MultipartBody.Part image);
}
