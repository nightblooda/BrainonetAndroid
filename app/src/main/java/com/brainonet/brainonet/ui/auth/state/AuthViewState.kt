package com.brainonet.brainonet.ui.auth.state

import com.brainonet.brainonet.models.AuthToken

data class AuthViewState(
    var otpGenerationFields: OTPGenerateFields? = OTPGenerateFields(),
    var otpAuthenticateFields: OTPAuthenticateFields? = OTPAuthenticateFields(),
    var authToken: AuthToken? = null,
    var otpCheck: Boolean = false
)

data class OTPGenerateFields(
    var phoneNumber: String? = null
){
    class OTPGenerateError{

        companion object{

            fun mustFillField(): String{
                return "Mobile number is required."
            }

            fun none(): String{
                return "None"
            }
        }
    }
    fun isValidForOTPGenerate(): String{

        if(phoneNumber.isNullOrEmpty()){
            return OTPGenerateError.mustFillField()
        }
        return OTPGenerateError.none()
    }

    override fun toString(): String{
        return "OTPGenerateState(phoneNumber=$phoneNumber)"
    }
}


data class OTPAuthenticateFields(
    var phoneNumber: String? = null,
    var otp: String? = null
){
    class OTPAuthenticateError{

        companion object{

            fun mustFillField(): String{
                return "Please enter OTP."
            }

            fun none(): String{
                return "None"
            }
        }
    }
    fun isValidForOTPAuthenticate(): String{

        if(phoneNumber.isNullOrEmpty() || otp.isNullOrEmpty()){
            return OTPAuthenticateError.mustFillField()
        }
        return OTPAuthenticateError.none()
    }

    override fun toString(): String{
        return "OTPAuthenticateState(phoneNumber=$phoneNumber, otp=$otp)"
    }
}