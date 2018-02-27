package com.immigration.restservices


import com.retrofitdemo.retrofit.model.ResponseModel
import com.retrofitdemo.retrofit.restservices.Constant.key_Fname
import com.retrofitdemo.retrofit.restservices.Constant.key_Lname
import com.retrofitdemo.retrofit.restservices.Constant.key_accessToken
import com.retrofitdemo.retrofit.restservices.Constant.key_contact
import com.retrofitdemo.retrofit.restservices.Constant.key_countryCode
import com.retrofitdemo.retrofit.restservices.Constant.urlUpdateProfile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface APIService {
   
   
   
   @FormUrlEncoded
   @POST("/user/login")
   fun Login(@Field("phone_number") phone_number:String,
             @Field("password")password:String,
             @Field("country_code") country_code:String
             ):Call<ResponseModel>
   
   
   
   @Multipart
   @POST("/immigration/api/updateProfile")
   fun postImage(@Header(key_accessToken) accessToken:String,
                 @Part image: MultipartBody.Part?,
                 @Part(key_Fname) firstName: RequestBody,
                 @Part(key_Lname) lastName: RequestBody,
                 @Part(key_contact) contact: RequestBody,
                 @Part(key_countryCode) countryCode: RequestBody
   ): Call<ResponseModel>
   
   
}