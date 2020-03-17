package com.brainonet.brainonet.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.brainonet.brainonet.api.GenericResponse
import com.brainonet.brainonet.api.main.ApiMainService
import com.brainonet.brainonet.models.AccountProperties
import com.brainonet.brainonet.models.AuthToken
import com.brainonet.brainonet.persistence.AccountPropertiesDao
import com.brainonet.brainonet.repository.JobManager
import com.brainonet.brainonet.repository.NetworkBoundResource
import com.brainonet.brainonet.session.SessionManager
import com.brainonet.brainonet.ui.DataState
import com.brainonet.brainonet.ui.Response
import com.brainonet.brainonet.ui.ResponseType
import com.brainonet.brainonet.ui.main.account.state.AccountViewState
import com.brainonet.brainonet.util.AbsentLiveData
import com.brainonet.brainonet.util.ApiSuccessResponse
import com.brainonet.brainonet.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val apiMainService: ApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
): JobManager("AccountRepository")
{

    val TAG: String = "AppDebug"

//    private var repositoryJob: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ){
            override suspend fun createCacheRequestAndReturn() {
                // If network is down then view cache and don't do network request
                withContext(Main){

                    //finish by viewing the db cache
                    result.addSource(loadFromCache()){viewState ->
                        onCompleteJob(
                            DataState.data(
                                data = viewState,
                                response = null
                            )
                        )

                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {

                updateLocalDb(response.body)

                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return apiMainService
                    .getAccountProperties(
                        "Token ${authToken.token!!}"
                    )
            }

            override fun setJob(job: Job) {
//                repositoryJob?.cancel()
//                repositoryJob = job
                addJob("getAccountProperties", job)
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap{
                        object: LiveData<AccountViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }


            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                cacheObject?.let{
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.pk,
                        cacheObject.first_name,
                        cacheObject.last_name,
                        cacheObject.phoneNumber
                    )
                }
            }

        }.asLiveData()
    }

    fun saveAccountProperties(
        authToken: AuthToken,
        pk: Int,
        first_name: String,
        last_name: String
    ): LiveData<DataState<AccountViewState>>{
        return object: NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = true,
            shouldLoadFromCache = false
        ){
            // Ignore in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {

                updateLocalDb(null)

                withContext(Main){

                    //finish with success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.response, responseType = ResponseType.Toast())
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return apiMainService.saveAccountProperties(
                    "Token ${authToken.token!!}",
                    first_name,
                    last_name
                )
            }

            // Ignore in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
                return accountPropertiesDao.saveAccountProperties(
                    pk,
                    first_name,
                    last_name
                )
            }

            override fun setJob(job: Job) {
//                repositoryJob?.cancel()
//                repositoryJob = job
                addJob("saveAccountProperties", job)
            }

        }.asLiveData()
    }

//    fun cancelActiveJobs(){
//        Log.d(TAG, "AccountRepository: cancelling on going jobs ...")
//    }
}