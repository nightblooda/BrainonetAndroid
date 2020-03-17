package com.brainonet.brainonet.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "account_properties")
data class AccountProperties(

    @SerializedName("pk")           //for retrofit
    @Expose                              //for retrofit
    @PrimaryKey(autoGenerate = false)    //for room persistence library
    @ColumnInfo(name = "pk")             //for room persistence library
    var pk: Int,

    @SerializedName("mobile_number")
    @Expose
    @ColumnInfo(name = "phoneNumber")
    var phoneNumber: String,

    @SerializedName("first_name")
    @Expose
    @ColumnInfo(name = "first_name")
    var first_name: String,

    @SerializedName("last_name")
    @Expose
    @ColumnInfo(name = "last_name")
    var last_name: String

)