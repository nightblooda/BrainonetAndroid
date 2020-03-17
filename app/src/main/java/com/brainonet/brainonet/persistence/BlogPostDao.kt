package com.brainonet.brainonet.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brainonet.brainonet.models.BlogPost
import com.brainonet.brainonet.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface BlogPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(blogPost: BlogPost): Long

    @Query("""
        SELECT * FROM blog_post
        WHERE title LIKE '%' || :query || '%'
        OR body LIKE '%' || :query || '%'
        LIMIT (:page * :pageSize)
    """)
    fun getAllBlogPost(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): LiveData<List<BlogPost>>

}