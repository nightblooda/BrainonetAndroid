package com.brainonet.brainonet.ui.auth

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.brainonet.brainonet.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseAuthFragment: DaggerFragment(){

    val TAG: String = "AppDebug"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run{
            ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        // (2)whenever switching to different fragment then cancel the active jobs of previous fragment.
//        cancelActiveJobs()
    }

    // (1)whenever switching to different fragment then cancel the active jobs of previous fragment.
//    private fun cancelActiveJobs(){
//        viewModel.cancelActiveJobs()
//    }
}