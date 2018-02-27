package com.retrofitprofile.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.immigration.restservices.APIService
import com.immigration.restservices.ApiUtils
import com.retrofitdemo.retrofit.model.ResponseModel
import com.retrofitprofile.R

class MainActivitySimple : AppCompatActivity() {
   
   lateinit var apiService: APIService
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
      apiService = ApiUtils.apiService
      initJson()
   }
   
   
   
   
   private fun initJson() {

      apiService.Login("9431410921", "12345678", "+91")
       .enqueue(object : retrofit2.Callback<ResponseModel> {
          
          override fun onResponse(call: retrofit2.Call<ResponseModel>?, response: retrofit2.Response<ResponseModel>?) {
            // Log.d("TAGS",response!!.body().message )
          }
          
          override fun onFailure(call: retrofit2.Call<ResponseModel>?, t: Throwable?) {
             Log.d("TAGS", t.toString())
          }
       })
   }
}

