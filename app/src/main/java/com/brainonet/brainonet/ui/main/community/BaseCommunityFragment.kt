package com.brainonet.brainonet.ui.main.community.state

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.brainonet.brainonet.R
import com.brainonet.brainonet.ui.DataStateChangeListener
import com.brainonet.brainonet.ui.main.community.viewmodel.CommunityViewModel
import com.brainonet.brainonet.viewmodels.ViewModelProviderFactory
import com.bumptech.glide.RequestManager
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseCommunityFragment : DaggerFragment(){

    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @Inject
    lateinit var requestManager: RequestManager

    lateinit var viewModel: CommunityViewModel

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.communityFragment, activity as AppCompatActivity)

        viewModel = activity?.run{
            ViewModelProvider(this, providerFactory).get(CommunityViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        cancelActiveJobs()
    }

    // (7)whenever switching to different fragment then cancel the active jobs of previous fragment.
    fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    private fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity){
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
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