package com.brainonet.brainonet.ui

data class Loading(val isLoading: Boolean)
data class Data<T>(val data: Event<T>?, val response: Event<Response>?)
data class StateError(val response: Response)

data class Response(val message: String?, val responseType: ResponseType)
sealed class ResponseType{

    class Toast: ResponseType()

    class Dialog: ResponseType()

    class None: ResponseType()
}

//Event class is used as wrapper for data that is exposed via LiveData on Event

open class Event<out T>(private val content: T){

    var hasBeenHandled = false
        private set // Allow external read but not write

    //Returns the content and prevent its use again.

    fun getContentIfNotHandled(): T?{
        return if(hasBeenHandled){
            null
        }else{
            hasBeenHandled = true
            content
        }
    }

    //Return the content even if it's already been handled

    fun peekContent(): T = content

    override fun toString(): String{
        return "Event(content=$content, hasBeenHandled=$hasBeenHandled"
    }

    companion object{

        private val TAG: String = "AppDebug"

        //we dont want event if the data is null
        fun <T>dataEvent(data: T?): Event<T>?{
            data?.let{
                return Event(it)
            }
            return null
        }

        //we dont want event if response is null
        fun responseEvent(response: Response?): Event<Response>?{
            response?.let{
                return Event(response)
            }
            return null
        }
    }


}