package com.brainonet.brainonet.di.auth

import android.content.SharedPreferences
import com.brainonet.brainonet.api.auth.ApiAuthService
import com.brainonet.brainonet.persistence.AccountPropertiesDao
import com.brainonet.brainonet.persistence.AuthTokenDao
import com.brainonet.brainonet.repository.auth.AuthRepository
import com.brainonet.brainonet.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule{

    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder: Retrofit.Builder): ApiAuthService{
        return retrofitBuilder
            .build()
            .create(ApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        apiAuthService: ApiAuthService,
        sharedPreferences: SharedPreferences,
        sharedPreferencesEditor: SharedPreferences.Editor
    ):AuthRepository{
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            apiAuthService,
            sessionManager,
            sharedPreferences,
            sharedPreferencesEditor
        )
    }
}