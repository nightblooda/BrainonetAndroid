package com.brainonet.brainonet.api.auth.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OTPGenerateResponse(

    @SerializedName("response")
    @Expose
    var response: String,

    @SerializedName("error_message")
    @Expose
    var errorMessage: String
){
    override fun toString(): String {
        return "OTPGenerateResponse(response='$response', error_message='$errorMessage')"
    }
}