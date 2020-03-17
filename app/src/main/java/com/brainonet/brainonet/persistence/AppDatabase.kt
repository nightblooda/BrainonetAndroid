package com.brainonet.brainonet.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.brainonet.brainonet.models.AccountProperties
import com.brainonet.brainonet.models.AuthToken
import com.brainonet.brainonet.models.BlogPost

@Database(entities = [AuthToken::class, AccountProperties::class, BlogPost::class], version = 1)//change version or uninstall app if there is any change in database
abstract  class AppDatabase: RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getBlogPostDao(): BlogPostDao

    companion object{

        const val DATABASE_NAME = "brainonet_db"
    }
}