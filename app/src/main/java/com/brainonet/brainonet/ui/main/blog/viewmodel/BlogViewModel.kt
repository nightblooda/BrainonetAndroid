package com.brainonet.brainonet.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.brainonet.brainonet.models.BlogPost
import com.brainonet.brainonet.repository.main.BlogRepository
import com.brainonet.brainonet.session.SessionManager
import com.brainonet.brainonet.ui.BaseViewModel
import com.brainonet.brainonet.ui.DataState
import com.brainonet.brainonet.ui.Loading
import com.brainonet.brainonet.ui.main.blog.state.BlogStateEvent
import com.brainonet.brainonet.ui.main.blog.state.BlogStateEvent.*
import com.brainonet.brainonet.ui.main.blog.state.BlogViewState
import com.brainonet.brainonet.util.AbsentLiveData
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val requestManager: RequestManager
): BaseViewModel<BlogStateEvent, BlogViewState>(){
    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when(stateEvent){

            is BlogSearchEvent -> {
                return sessionManager.cachedToken.value?.let{ authToken ->
                    blogRepository.searchBlogPosts(
                        authToken = authToken,
                        query = getSearchQuery(),
                        page = getPage()
                    )
                }?: AbsentLiveData.create()
            }

            is None -> {
                return object: LiveData<DataState<BlogViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                        //check from AccounViewModel which is better
                    }
                }
            }
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }



    fun cancelActiveJobs(){
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}