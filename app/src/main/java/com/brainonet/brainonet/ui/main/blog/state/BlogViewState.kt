package com.brainonet.brainonet.ui.main.blog.state

import com.brainonet.brainonet.models.BlogPost

data class BlogViewState (

    // BlogFragment vars
    var blogFields: BlogFields = BlogFields(),


    //ViewBlogFragment vars
    var viewBlogFields: ViewBlogFields = ViewBlogFields()

    // CommentFragment vars


){
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false

    )

    data class ViewBlogFields(
        var blogPost: BlogPost? = null
    )
}