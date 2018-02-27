package com.immigration.restservices


import com.retrofitdemo.retrofit.model.ResponseModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface APIService {

/*
    @Multipart
    @POST(urlUpdateProfile)
    fun postImage(@Header (key_accessToken) accessToken:String,
                  @Part image: MultipartBody.Part?,
                  @Part(key_Fname) firstName: RequestBody,
                  @Part(key_Lname) lastName: RequestBody,
                  @Part(key_contact) contact: RequestBody,
                  @Part(key_countryCode) countryCode: RequestBody
                 ): Call<ResponseModel>*/


    @Multipart
    @POST("/apiAvatar")
    fun multipleImageUpload(
            @Part image: MultipartBody.Part): Call<ResponseModel>

}


 /*

     @Headers("Content-Type: application/json")
     @POST(urlChangePassword)
     fun changePasswords(@Header ("accessToken") accessToken:String,
                        @Body body: Map<String, String>): Call<ResponseModel>


     @Headers("Content-Type: application/json")
     @GET(urlLogout)
     fun logout(@Header ("accessToken") accessToken:String): Call<ResponseModel>




    @FormUrlEncoded
    @POST("/")
    fun Save(@Field("answer") name:String,
             @Field("Date") Date:String):Call<JSONObject>
*/


