package com.brainonet.brainonet.ui.main.community.state

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.brainonet.brainonet.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment

abstract class BaseCommunityFragment : DaggerFragment(){

    val TAG: String = "AppDebug"

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelActiveJobs()
    }

    // (7)whenever switching to different fragment then cancel the active jobs of previous fragment.
    fun cancelActiveJobs(){
//        viewModel.cancelActiveJobs()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }
}