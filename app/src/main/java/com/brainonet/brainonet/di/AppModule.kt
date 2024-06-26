package com.brainonet.brainonet.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.brainonet.brainonet.R
import com.brainonet.brainonet.persistence.AccountPropertiesDao
import com.brainonet.brainonet.persistence.AppDatabase
import com.brainonet.brainonet.persistence.AppDatabase.Companion.DATABASE_NAME
import com.brainonet.brainonet.persistence.AuthTokenDao
import com.brainonet.brainonet.util.Constants
import com.brainonet.brainonet.util.LiveDataCallAdapter
import com.brainonet.brainonet.util.LiveDataCallAdapterFactory
import com.brainonet.brainonet.util.PreferenceKeys
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule{

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences{
        return application.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor{
        return sharedPreferences.edit()
    }

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson{
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(gson: Gson): Retrofit.Builder{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))

    }

    @Singleton
    @Provides
    fun provideAppDb(app: Application): AppDatabase{
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthTokenDao(db: AppDatabase): AuthTokenDao{
        return db.getAuthTokenDao()
    }

    @Singleton
    @Provides
    fun provideAccountPropertiesDao(db: AppDatabase): AccountPropertiesDao{
        return db.getAccountPropertiesDao()
    }

    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions{
        return RequestOptions
            .placeholderOf(R.drawable.default_image)
            .error(R.drawable.default_image)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(application: Application, requestOptions: RequestOptions): RequestManager{
        return Glide.with(application)
            .setDefaultRequestOptions(requestOptions)
    }
}