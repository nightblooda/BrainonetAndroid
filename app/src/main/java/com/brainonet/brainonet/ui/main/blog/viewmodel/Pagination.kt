package com.brainonet.brainonet.ui.main.blog.viewmodel

import android.util.Log
import com.brainonet.brainonet.ui.main.blog.state.BlogStateEvent
import com.brainonet.brainonet.ui.main.blog.state.BlogViewState

fun BlogViewModel.resetPage(){
    val update = getCurrentViewStateOrNew()
    update.blogFields.page = 1
    setViewState(update)
}

fun BlogViewModel.loadFirstPage(){
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    setStateEvent(BlogStateEvent.BlogSearchEvent())
}

fun BlogViewModel.incrementPageNumber(){
    val update = getCurrentViewStateOrNew()
    val page = update.copy().blogFields.page
    update.blogFields.page = page + 1
    setViewState(update)
}

fun BlogViewModel.nextPage(){
    if(!getIsQueryExhausted() && !getIsQueryInProgress()){
        Log.d(TAG, "BlogViewModel: Attempting to load next page...")
        incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(BlogStateEvent.BlogSearchEvent())
    }

}

fun BlogViewModel.handleIncomingBlogListData(viewState: BlogViewState){
    setQueryExhausted(viewState.blogFields.isQueryExhausted)
    setQueryInProgress(viewState.blogFields.isQueryInProgress)
    setBlogListData(viewState.blogFields.blogList)
}