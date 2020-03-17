package com.brainonet.brainonet.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.brainonet.brainonet.api.auth.network_responses.LoginResponse
import com.brainonet.brainonet.api.auth.network_responses.OTPGenerateResponse
import com.brainonet.brainonet.api.auth.network_responses.RegistrationResponse
import com.brainonet.brainonet.models.AuthToken
import com.brainonet.brainonet.repository.auth.AuthRepository
import com.brainonet.brainonet.ui.BaseViewModel
import com.brainonet.brainonet.ui.DataState
import com.brainonet.brainonet.ui.auth.state.AuthStateEvent
import com.brainonet.brainonet.ui.auth.state.AuthStateEvent.*
import com.brainonet.brainonet.ui.auth.state.AuthViewState
import com.brainonet.brainonet.ui.auth.state.OTPAuthenticateFields
import com.brainonet.brainonet.ui.auth.state.OTPGenerateFields
import com.brainonet.brainonet.util.AbsentLiveData
import com.brainonet.brainonet.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): BaseViewModel<AuthStateEvent, AuthViewState>() {

//
//    fun testLogin(): LiveData<GenericApiResponse<OTPGenerateResponse>> {
//        return authRepository.testLoginRequest(
//            "+919106889708"
//        )
//    }
//
//    fun testRegister(): LiveData<GenericApiResponse<RegistrationResponse>>{
//        return authRepository.testRegistrationRequest(
//            "himanshu",
//            "choudhary"
//        )
//    }


    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){

            is OTPGenerateAttemptEvent -> {
                return authRepository.attemptOTPGenerate(
                    stateEvent.phoneNumber
                )
            }

            is OTPAuthenticateAttemptEvent -> {
                return authRepository.attemptOTPAuthentication(
                    stateEvent.phoneNumber,
                    stateEvent.otp
                )
            }

            is CheckPreviousAuthEvent -> {
                return authRepository.checkPreviousAuthUser()
            }

            is None -> {
                return object: LiveData<DataState<AuthViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setOTPGenerateFields(otpGenerateFields: OTPGenerateFields){
        val update = getCurrentViewStateOrNew()
        if(update.otpGenerationFields == otpGenerateFields){
            return
        }
        update.otpGenerationFields = otpGenerateFields
        _viewState.value = update
    }

    fun setOTPAuthenticateFields(otpAuthenticateFields: OTPAuthenticateFields){
        val update = getCurrentViewStateOrNew()
        if(update.otpAuthenticateFields == otpAuthenticateFields){
            return
        }
        update.otpAuthenticateFields = otpAuthenticateFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken){
        val update = getCurrentViewStateOrNew()
        if(update.authToken == authToken){
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    fun setOtpCheck(otpCheck: Boolean){
        val update = getCurrentViewStateOrNew()
        update.otpCheck = otpCheck
        _viewState.value = update
    }

    fun cancelActiveJobs(){
        handelPendingData()
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    // hiding progress bar
    //(3) whenever switching to different fragment then cancel the active jobs of previous fragment.
    fun handelPendingData(){
        setStateEvent(None())
    }
}