package com.retrofitdemo.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseModel {
   @SerializedName("message")
   @Expose
   var message: String? = null
   @SerializedName("result")
   @Expose
   var result: Result? = null
}