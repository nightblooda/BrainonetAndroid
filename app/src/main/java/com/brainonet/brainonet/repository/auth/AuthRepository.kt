package com.brainonet.brainonet.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.brainonet.brainonet.api.auth.ApiAuthService
import com.brainonet.brainonet.api.auth.network_responses.LoginResponse
import com.brainonet.brainonet.api.auth.network_responses.OTPGenerateResponse
import com.brainonet.brainonet.api.auth.network_responses.RegistrationResponse
import com.brainonet.brainonet.models.AccountProperties
import com.brainonet.brainonet.models.AuthToken
import com.brainonet.brainonet.persistence.AccountPropertiesDao
import com.brainonet.brainonet.persistence.AuthTokenDao
import com.brainonet.brainonet.repository.JobManager
import com.brainonet.brainonet.repository.NetworkBoundResource
import com.brainonet.brainonet.session.SessionManager
import com.brainonet.brainonet.ui.Data
import com.brainonet.brainonet.ui.DataState
import com.brainonet.brainonet.ui.Response
import com.brainonet.brainonet.ui.ResponseType
import com.brainonet.brainonet.ui.auth.state.AuthViewState
import com.brainonet.brainonet.ui.auth.state.OTPAuthenticateFields
import com.brainonet.brainonet.ui.auth.state.OTPGenerateFields
import com.brainonet.brainonet.util.*
import com.brainonet.brainonet.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.brainonet.brainonet.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.brainonet.brainonet.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.brainonet.brainonet.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.google.android.gms.auth.api.Auth
import kotlinx.coroutines.Job

class AuthRepository
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val apiAuthService: ApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPreferencesEditor: SharedPreferences.Editor
): JobManager("AuthRepository")
{

    private val TAG: String = "AppDebug"

//    private var repositoryJob: Job? = null

    fun attemptOTPGenerate(phoneNumber: String): LiveData<DataState<AuthViewState>>{

        val OTPGenerateFieldErrors = OTPGenerateFields(phoneNumber).isValidForOTPGenerate()
        if(!OTPGenerateFieldErrors.equals(OTPGenerateFields.OTPGenerateError.none())){
            return returnErrorResponse(OTPGenerateFieldErrors, ResponseType.Dialog())
        }

        return object: NetworkBoundResource<OTPGenerateResponse, Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<OTPGenerateResponse>) {

                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            otpCheck = true
                        ),
                        response = Response(response.body.response, ResponseType.Toast())
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<OTPGenerateResponse>> {
                Log.d(TAG, "InNetworkBoundResource: createCall()")
                return apiAuthService.OTPGenerate(phoneNumber)
            }

            override fun setJob(job: Job) {
//                repositoryJob?.cancel()
//                repositoryJob = job
                addJob("attemptOTPGenerate", job)
            }

            // Ignore in this case
            override suspend fun createCacheRequestAndReturn() {

            }
            // Ignore in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // Ignore in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }
        }.asLiveData()
    }

    fun attemptOTPAuthentication(phoneNumber: String, otp: String): LiveData<DataState<AuthViewState>>{

        val OTPAuthenticationErrors = OTPAuthenticateFields(phoneNumber, otp).isValidForOTPAuthenticate()
        if(!OTPAuthenticationErrors.equals(OTPAuthenticateFields.OTPAuthenticateError.none())){
            return returnErrorResponse(OTPAuthenticationErrors, ResponseType.Dialog())
        }

        return object: NetworkBoundResource<LoginResponse, Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {

                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if(response.body.response.equals(GENERIC_AUTH_ERROR)){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                //don't care about result. Just insert if it doesn't exist b/c foreign key relationship
                //with AuthToken table
                accountPropertiesDao.insertOrIgnore(
                        AccountProperties(
                            response.body.pk,
                            response.body.phoneNumber,
                            response.body.first_name,
                            response.body.last_name
                        )
                )

                //will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if(result < 0){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                saveAuthenticatedUserToPrefs(phoneNumber)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                Log.d(TAG, "InNetworkBoundResource: createCall(), OTP: ${otp}, phoneNumber: ${phoneNumber}")
                return apiAuthService.OTPAuthentication(phoneNumber, otp)
            }

            override fun setJob(job: Job) {
//                repositoryJob?.cancel()
//                repositoryJob = job
                addJob("attemptOTPAuthentication", job)
            }

            // Ignore in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            // Ignore in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // Ignore in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }
        }.asLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>>{
        val previousAuthUserPhoneNumber: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if(previousAuthUserPhoneNumber.isNullOrBlank()){
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found...")
            return returnNoTokenFound()
        }

        return object: NetworkBoundResource<Void, Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false,
            false,
            false
        ){
            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDao.searchByPhoneNumber(previousAuthUserPhoneNumber).let{accountProperties ->

                    Log.d(TAG, "checkPreviousAuthUser: searching for token: $accountProperties")

                    accountProperties?.let{
                        if(accountProperties.pk > -1){
                            authTokenDao.searchByPk(accountProperties.pk).let{ authToken ->

                                if(authToken != null){
                                    if(authToken.token != null) {
                                        onCompleteJob(
                                            DataState.data(
                                                data = AuthViewState(
                                                    authToken = authToken
                                                )
                                            )
                                        )
                                        return
                                    }
                                }
                            }
                        }
                    }
                    Log.d(TAG, "checkPreviousAuthUser: AuthToken not found...")
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )

                }
            }

            //Ignore in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
            }

            //Ignore in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
//                repositoryJob?.cancel()
//                repositoryJob = job
                addJob("checkPreviousAuthUser", job)
            }

            // Ignore in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // Ignore in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object: LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None())
                )
            }
        }
    }


    private fun returnErrorResponse(errorMessage: String, responseType: ResponseType): LiveData<DataState<AuthViewState>>{
        Log.d(TAG, "returnErrorResponse: ${errorMessage}")

        return object: LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response (
                        errorMessage,
                        responseType
                    )
                )
            }
        }
    }

//    fun cancelActiveJobs(){
//        Log.d(TAG, "AuthRepository: Cancelling on-going jobs...")
//        repositoryJob?.cancel()
//    }

    private fun saveAuthenticatedUserToPrefs(phoneNumber: String) {
        sharedPreferencesEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, phoneNumber)
        sharedPreferencesEditor.apply()
    }


//    fun attemptOTPGenerate(phoneNumber: String): LiveData<DataState<AuthViewState>> {
//        return apiAuthService.OTPGenerate(phoneNumber)
//            .switchMap { response ->
//                object: LiveData<DataState<AuthViewState>>(){
//                    override fun onActive() {
//                        super.onActive()
//                        when(response){
//                            is ApiSuccessResponse -> {
//                                value = DataState.data(
//                                    AuthViewState(
//                                        authToken = AuthToken(response.body.pk, response.body.token)
//                                    ),
//                                    response = null
//                                )
//                            }
//                            is ApiErrorResponse -> {
//                                value = DataState.error(
//                                    Response(
//                                        message = response.errorMessage,
//                                        responseType = ResponseType.Dialog()
//                                    )
//                                )
//                            }
//                            is ApiEmptyResponse -> {
//                                value = DataState.error(
//                                    Response(
//                                        message = ERROR_UNKNOWN,
//                                        responseType =  ResponseType.Dialog()
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//    }

}