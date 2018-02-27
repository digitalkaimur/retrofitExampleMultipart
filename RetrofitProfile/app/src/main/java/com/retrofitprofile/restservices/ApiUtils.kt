package com.immigration.restservices

import com.retrofitdemo.retrofit.restservices.Constant.BASE_URL
import com.retrofitdemo.retrofit.restservices.Constant.BASE_URL2

object ApiUtils {

        val apiService: APIService
        get() = RetrofitClient().getClient(BASE_URL)!!.create(APIService::class.java)

        val apiService2: APIService
        get() = RetrofitClient().getClient(BASE_URL2)!!.create(APIService::class.java)
}