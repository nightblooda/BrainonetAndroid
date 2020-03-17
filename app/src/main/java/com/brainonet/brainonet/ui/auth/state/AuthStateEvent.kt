package com.brainonet.brainonet.ui.auth.state

sealed class AuthStateEvent{

    data class OTPGenerateAttemptEvent(
        val phoneNumber: String
    ): AuthStateEvent()

    data class OTPAuthenticateAttemptEvent(
        val phoneNumber: String,
        val otp: String
    ): AuthStateEvent()

    class CheckPreviousAuthEvent(): AuthStateEvent()

    class None: AuthStateEvent()
}