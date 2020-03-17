package com.brainonet.brainonet.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.brainonet.brainonet.R
import com.brainonet.brainonet.ui.BaseActivity
import com.brainonet.brainonet.ui.main.MainActivity
import com.brainonet.brainonet.ui.auth.state.AuthStateEvent
import com.brainonet.brainonet.util.AppSignatureHelper
import com.brainonet.brainonet.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.brainonet.brainonet.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_mobile.*
import javax.inject.Inject

class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener{

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }

    internal var autoLogin: AutoLogin? = null
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)
        subscribeObservers()

        // (9)whenever switching to different fragment then cancel the active jobs of previous fragment.
        checkPreviousAuthUser()

//        autoLogin = AutoLogin(this)
//        autoLogin!!.requestHint()
        val appSignatureHelper = AppSignatureHelper(applicationContext)
        appSignatureHelper.appSignatures

        Log.d(TAG, "HASH STRING: " + appSignatureHelper.appSignatures)


    }

    override fun expandAppBar() {
        // Ignore in this case
    }

    // (10)whenever switching to different fragment then cancel the active jobs of previous fragment.
//    override fun onResume() {
//        super.onResume()
//        checkPreviousAuthUser()
//    }

    fun subscribeObservers() {

        viewModel.dataState.observe(this, Observer { dataState ->
            onDataStateChange(dataState)
            dataState.data?.let {data ->
                data.data?.let{ event ->
                    event.getContentIfNotHandled()?.let{
                        it.authToken?.let{
                            Log.d(TAG, "AuthActivity, DataState: ${it}")
                            viewModel.setAuthToken(it)
                        }
                        it.otpCheck?.let{
                            Log.d(TAG, "AuthActivity, DataState: ${it}")
                            viewModel.setOtpCheck(it)
                        }
                    }
                }
                data.response?.let{event ->
                    event.peekContent().let{ response ->
                        response.message?.let{ message ->
                            if(message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)){
                                onFinishCheckPreviousAuthUser()
                            }
                        }
                    }
                }


            }
        })



        viewModel.viewState.observe(this, Observer {
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthViewState: ${it}")
            it.authToken?.let{
                sessionManager.login(it)
            }
        })

        sessionManager.cachedToken.observe(this, Observer{dataState ->
            Log.d(TAG, "AuthActivity: subscribeObservers: AuthToken: ${dataState}")
            dataState.let{ authToken ->
                if(authToken != null && authToken.account_pk != -1 && authToken.token != null){
                    navMainActivity()
                }
            }

        })
    }

    fun checkPreviousAuthUser(){
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }

    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
        autoLogin = AutoLogin(this)
        autoLogin!!.requestHint()
    }

    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun displayProgressBar(bool: Boolean) {
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }else{
            progress_bar.visibility = View.GONE
        }
    }

    private val CREDENTIAL_PICKER_REQUEST = 1  // Set to an unused request code


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST ->
                // Obtain the phone number from the result
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val new_number = autoLogin!!.getPhoneNo(data).substring(3)
                    mobile_number.setText(new_number)
                }
        }
    }




}
