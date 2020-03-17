package com.brainonet.brainonet.api.auth

import androidx.lifecycle.LiveData
import com.brainonet.brainonet.api.auth.network_responses.LoginResponse
import com.brainonet.brainonet.api.auth.network_responses.OTPGenerateResponse
import com.brainonet.brainonet.api.auth.network_responses.RegistrationResponse
import com.brainonet.brainonet.util.GenericApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiAuthService {

    @POST("account/getOTP/")
    @FormUrlEncoded
    fun OTPGenerate(
        @Field("phoneNumber") phoneNumber: String
    ): LiveData<GenericApiResponse<OTPGenerateResponse>>

    @POST("account/authOTP/")
    @FormUrlEncoded
    fun OTPAuthentication(
        @Field("mobile_number") mobile_number: String,
        @Field("otp") otp: String
    ): LiveData<GenericApiResponse<LoginResponse>>


    @POST("account/register")
    @FormUrlEncoded
    fun register(
        @Field("firstname") firstname: String,
        @Field("lastname") lastname: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>

}