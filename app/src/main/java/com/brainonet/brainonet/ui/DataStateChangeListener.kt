package com.brainonet.brainonet.ui

interface DataStateChangeListener{

    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()


    fun hideSoftKeyboard()
}