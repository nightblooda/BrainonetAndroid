package com.brainonet.brainonet.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brainonet.brainonet.models.AccountProperties

@Dao
interface AccountPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountProperties: AccountProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountProperties: AccountProperties): Long

    @Query("SELECT * FROM account_properties WHERE pk = :pk")
    fun searchByPk(pk: Int): LiveData<AccountProperties>

    @Query("SELECT * FROM account_properties WHERE phoneNumber = :phoneNumber")
    fun searchByPhoneNumber(phoneNumber: String): AccountProperties?

    @Query("UPDATE account_properties SET first_name = :first_name, last_name = :last_name, phoneNumber = :phoneNumber WHERE pk = :pk")
    fun updateAccountProperties(pk: Int, first_name: String, last_name: String, phoneNumber: String)

    @Query("UPDATE account_properties SET first_name = :first_name, last_name = :last_name WHERE pk = :pk")
    fun saveAccountProperties(pk: Int, first_name: String, last_name: String)
}