package com.healthy.basket.network;

import com.healthy.basket.Getset.ForgetPassGetSet;
import com.healthy.basket.Getset.MedicalOrderModel;
import com.healthy.basket.Getset.UploadMedicalResult;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("imageapi.php")
    Call<MedicalOrderModel> getMedicalOrder(@Query("user_id") String user_id);

    @Multipart
    @POST("upload_pre.php")
    Call<UploadMedicalResult> uploadImage(@Part("file\"; filename=\"myfile.jpg\" ") RequestBody file, @Part("desc") RequestBody note, @Part("user_id") RequestBody user_id);

    @GET("forgotpwd.php")
    Call<ForgetPassGetSet> forgetPass(@Query("phone_no") String phone_no);


}
