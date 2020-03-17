package com.brainonet.brainonet.api.auth.network_responses

import com.google.android.gms.common.internal.ConnectionErrorMessages
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RegistrationResponse(

    @SerializedName("response")
    @Expose
    var response: String,

    @SerializedName("error_message")
    @Expose
    var errorMessages: String,

    @SerializedName("firstname")
    @Expose
    var firstname: String,

    @SerializedName("lastname")
    @Expose
    var lastname: String,

    @SerializedName("pk")
    @Expose
    var pk: Int,

    @SerializedName("token")
    @Expose
    var token: String
){
    override fun toString(): String {
        return "RegistrationResponse(response'$response', errorMessage='$errorMessages', firstname='$firstname', lastname='$lastname', token='$token')"
    }
}