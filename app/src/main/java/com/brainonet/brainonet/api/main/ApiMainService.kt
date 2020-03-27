package com.brainonet.brainonet.api.main

import androidx.lifecycle.LiveData
import com.brainonet.brainonet.api.GenericResponse
import com.brainonet.brainonet.api.main.responses.BlogListSearchResponse
import com.brainonet.brainonet.api.main.responses.CommunityListSearchResponse
import com.brainonet.brainonet.models.AccountProperties
import com.brainonet.brainonet.util.GenericApiResponse
import retrofit2.http.*

interface ApiMainService {

    @GET("account/accountProperties/")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

    @PUT("account/updateAccountProperties/")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String
    ): LiveData<GenericApiResponse<GenericResponse>>

    @GET("blog/list")
    fun searchListBlogPosts(
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("page") page: Int
    ): LiveData<GenericApiResponse<BlogListSearchResponse>>

    @GET("communities/list")
    fun searchListCommunity(
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("page") page: Int
    ): LiveData<GenericApiResponse<CommunityListSearchResponse>>

}