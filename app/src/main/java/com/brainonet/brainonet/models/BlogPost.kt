package com.brainonet.brainonet.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blog_post")
data class BlogPost(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    var pk: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "slug")
    var slug: String,

    @ColumnInfo(name = "body")
    var body: String,

    @ColumnInfo(name = "image")
    var image: String,

    @ColumnInfo(name = "data_updated")
    var date_updated: Long,

    @ColumnInfo(name = "community")
    var community: String
){
    override fun toString(): String {
        return "BlogPost(pk=$pk, title=$title, " +
                "slug=$slug, " +
                "body=$body, " +
                "image=$image, " +
                "date_updated=$date_updated, " +
                "community=$community)"

    }
}