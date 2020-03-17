package com.brainonet.brainonet.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.brainonet.brainonet.api.main.ApiMainService
import com.brainonet.brainonet.api.main.responses.BlogListSearchResponse
import com.brainonet.brainonet.models.AuthToken
import com.brainonet.brainonet.models.BlogPost
import com.brainonet.brainonet.persistence.BlogPostDao
import com.brainonet.brainonet.repository.JobManager
import com.brainonet.brainonet.repository.NetworkBoundResource
import com.brainonet.brainonet.session.SessionManager
import com.brainonet.brainonet.ui.DataState
import com.brainonet.brainonet.ui.main.blog.state.BlogViewState
import com.brainonet.brainonet.util.ApiSuccessResponse
import com.brainonet.brainonet.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.brainonet.brainonet.util.DateUtils
import com.brainonet.brainonet.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val apiMainService: ApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager("BlogRepository"){

    private val TAG: String = "AppDebug"

    fun searchBlogPosts(
        authToken: AuthToken,
        query: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main){
                    // finish by viewing in db cache
                    result.addSource(loadFromCache()){ viewState ->
                        viewState.blogFields.isQueryInProgress = false
                        if(page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size){
                            viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {

                val blogPostList: ArrayList<BlogPost> = ArrayList()
                for(blogPostResponse in response.body.results){
                    blogPostList.add(
                        BlogPost(
                            pk = blogPostResponse.pk,
                            title = blogPostResponse.title,
                            slug = blogPostResponse.slug,
                            body = blogPostResponse.body,
                            image = blogPostResponse.image,
                            date_updated = DateUtils.convertServerStringDateToLong(
                                blogPostResponse.date_updated
                            ),
                            community = blogPostResponse.community
                        )
                    )
                }
                updateLocalDb(blogPostList)

                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return apiMainService.searchListBlogPosts(
                    "Token ${authToken.token!!}",
                    query = query,
                    page = page
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.getAllBlogPost(
                    query = query,
                    page = page
                )
                    .switchMap {
                        object: LiveData<BlogViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    BlogViewState.BlogFields(
                                        blogList = it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                if(cacheObject != null){
                    withContext(IO){
                        for(blogPost in cacheObject){
                            try{
                                // launch each insert as a separete job to executed in paralledl
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting blog: $blogPost")
                                    blogPostDao.insert(blogPost)
                                }
                            }catch(e: Exception){
                                Log.e(TAG, "updateLocalDb: error updateing cache with slug: ${blogPost.slug}")
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }

        }.asLiveData()
    }
}