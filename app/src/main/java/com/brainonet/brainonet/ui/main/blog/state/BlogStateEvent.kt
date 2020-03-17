package com.brainonet.brainonet.ui.main.blog.state

sealed class BlogStateEvent {

    class BlogSearchEvent : BlogStateEvent()


    class None : BlogStateEvent()

}