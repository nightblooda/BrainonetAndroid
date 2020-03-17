package com.brainonet.brainonet.di.main

import com.brainonet.brainonet.api.main.ApiMainService
import com.brainonet.brainonet.persistence.AccountPropertiesDao
import com.brainonet.brainonet.persistence.AppDatabase
import com.brainonet.brainonet.persistence.BlogPostDao
import com.brainonet.brainonet.repository.main.AccountRepository
import com.brainonet.brainonet.repository.main.BlogRepository
import com.brainonet.brainonet.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideApiMainService(retrofitBuilder: Retrofit.Builder): ApiMainService{
        return retrofitBuilder
            .build()
            .create(ApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        apiMainService: ApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository{
        return AccountRepository(
            apiMainService,
            accountPropertiesDao,
            sessionManager
        )
    }

    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        apiMainService: ApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository {
        return BlogRepository(apiMainService, blogPostDao, sessionManager)
    }



}