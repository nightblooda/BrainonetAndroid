package com.brainonet.brainonet.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.brainonet.brainonet.R
import com.brainonet.brainonet.models.AccountProperties
import com.brainonet.brainonet.session.SessionManager
import com.brainonet.brainonet.ui.main.account.state.AccountStateEvent
import com.brainonet.brainonet.ui.main.blog.BaseBlogFragment
import kotlinx.android.synthetic.main.fragment_update_account.*
import javax.inject.Inject

class UpdateAccountFragment : BaseAccountFragment(){

//    @Inject
//    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()

        submit_fields.setOnClickListener {
            saveChanges()
        }

        logout_btn.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)
            Log.d(TAG, "UpdateAccountAccountFragment, DataState: ${dataState}:")
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            if(viewState != null){
                viewState.accountProperties?.let{
                    Log.d(TAG, "UpdateAccountFragment, ViewState: ${it}")
                    setAccountDataFields(it)
                }
            }
        })
    }

    private fun setAccountDataFields(accountProperties: AccountProperties){
        if(input_first_name.text.isNullOrBlank()){
            input_first_name.setText(accountProperties.first_name)
        }
        if(input_last_name.text.isNullOrBlank()){
            input_last_name.setText(accountProperties.last_name)
        }
        mobile_number.text = accountProperties.phoneNumber
    }

    private fun saveChanges(){
        viewModel.setStateEvent(
                AccountStateEvent.UpdateAccountPropertiesEvent(
                    input_first_name.text.toString(),
                    input_last_name.text.toString()
                )
        )
        stateChangeListener.hideSoftKeyboard()
    }
}