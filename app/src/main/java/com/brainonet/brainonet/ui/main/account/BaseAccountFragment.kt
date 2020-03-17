package com.brainonet.brainonet.ui.main.account

import android.accounts.Account
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
import com.brainonet.brainonet.ui.main.MainActivity
import com.brainonet.brainonet.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseAccountFragment : DaggerFragment(){

    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AccountViewModel


    lateinit var stateChangeListener: DataStateChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)

        viewModel = activity?.run{
            ViewModelProvider(this, providerFactory).get(AccountViewModel::class.java)
        }?: throw Exception("Invalid activity")
        // SWITCHING TO DIFFERENT FRAGMENT HIDE PROGRESS BAR AND CANCEL ALL THE REQUEST
        cancelActiveJobs()
    }

    // (5)whenever switching to different fragment then cancel the active jobs of previous fragment.
    fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }


//    fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity){
//        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
//        NavigationUI.setupActionBarWithNavController(
//            activity,
//            findNavController(),
//            appBarConfiguration
//        )
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }
}