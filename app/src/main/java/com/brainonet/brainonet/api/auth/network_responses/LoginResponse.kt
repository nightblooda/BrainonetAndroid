package com.brainonet.brainonet.api.auth.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse(

    @SerializedName("response")
    @Expose
    var response: String,

    @SerializedName("error_message")
    @Expose
    var errorMessage: String,

    @SerializedName("token")
    @Expose
    var token: String,

    @SerializedName("pk")
    @Expose
    var pk: Int,

    @SerializedName("phoneNumber")
    @Expose
    var phoneNumber: String,

    @SerializedName("first_name")
    @Expose
    var first_name: String,

    @SerializedName("last_name")
    @Expose
    var last_name: String
){
    override fun toString(): String {
        return "LoginResponse(response='$response', errorMessage='$errorMessage', token='$token', pk=$pk, phoneNumber='$phoneNumber')"
    }
}