package com.retrofitdemo.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result {
   @SerializedName("userId")
   @Expose
   var userId: String? = null
   @SerializedName("email")
   @Expose
   var email: String? = null
   @SerializedName("countryCode")
   @Expose
   var countryCode: String? = null
   @SerializedName("contact")
   @Expose
   var contact: String? = null
   @SerializedName("accessToken")
   @Expose
   var accessToken: String? = null
   @SerializedName("profilePic")
   @Expose
   var profilePic: String? = null
   @SerializedName("firstName")
   @Expose
   var firstName: String? = null
   @SerializedName("lastName")
   @Expose
   var lastName: String? = null
   @SerializedName("isVerified")
   @Expose
   var isVerified: String? = null
   @SerializedName("isProfileCreated")
   @Expose
   var isProfileCreated: String? = null
}