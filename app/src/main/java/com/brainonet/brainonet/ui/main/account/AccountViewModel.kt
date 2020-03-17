package com.brainonet.brainonet.ui.main.account

import androidx.lifecycle.LiveData
import com.brainonet.brainonet.models.AccountProperties
import com.brainonet.brainonet.repository.main.AccountRepository
import com.brainonet.brainonet.session.SessionManager
import com.brainonet.brainonet.ui.BaseViewModel
import com.brainonet.brainonet.ui.DataState
import com.brainonet.brainonet.ui.main.account.state.AccountStateEvent
import com.brainonet.brainonet.ui.main.account.state.AccountStateEvent.*
import com.brainonet.brainonet.ui.main.account.state.AccountViewState
import com.brainonet.brainonet.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
): BaseViewModel<AccountStateEvent, AccountViewState>(){
    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent){

            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let{ authToken ->
                    accountRepository.getAccountProperties(authToken)
                }?: AbsentLiveData.create()
            }

            is UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let{ authToken ->
                    authToken.account_pk.let{ pk ->
                        accountRepository.saveAccountProperties(
                            authToken,
                            pk!!,
                            stateEvent.first_name,
                            stateEvent.last_name
                        )
                    }
                }?: AbsentLiveData.create()
            }

            is None -> {
                return return object: LiveData<DataState<AccountViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                        // check from BlogViewModel which is better
                    }
                }
            }
        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties){
        val update = getCurrentViewStateOrNew()
        if(update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logout(){
        sessionManager.logout()
    }

    fun cancelActiveJobs(){
        handelPendingData()
        accountRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    // hiding progress bar
    //(4) whenever switching to different fragment then cancel the active jobs of previous fragment.
    fun handelPendingData(){
        setStateEvent(None())
    }


}