package com.brainonet.brainonet.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.brainonet.brainonet.R
import com.brainonet.brainonet.ui.FragmentFirst
import com.brainonet.brainonet.ui.auth.state.AuthStateEvent
import kotlinx.android.synthetic.main.fragment_mobile.*
import kotlinx.android.synthetic.main.fragment_otp.*
import java.util.regex.Pattern

class OtpFragment : BaseAuthFragment() {
    lateinit var autoLogin: AutoLogin
    private var receivedOtp: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "OtpFragment: ${viewModel.hashCode()}")
        autoLogin = AutoLogin(getView()!!.context)

        val textView = view.findViewById<TextView>(R.id.phone_number)
        textView.text = arguments?.getString("phone_number")

        subscribeObservers()

        resend_btn.setOnClickListener {
            resendOtp()
        }

        verify_btn.setOnClickListener {
            verifyOTP()
        }

        getOtpMessage()
    }

    fun verifyOTP(){
        viewModel.setStateEvent(
            AuthStateEvent.OTPAuthenticateAttemptEvent(
                phoneNumber = arguments?.getString("phone_number").toString(),
                otp = txt_otp.text.toString()
            )
        )
    }

    fun resendOtp(){
        getOtpMessage()
        viewModel.setStateEvent(
            AuthStateEvent.OTPGenerateAttemptEvent(
                arguments?.getString("phone_number").toString()
            )
        )

        Log.d(TAG, "resend on : ${arguments?.getString("phone_number").toString()}")
    }

    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.otpAuthenticateFields?.let{
                it.otp?.let{txt_otp.setText(it)}
            }
        })
        Log.d(TAG, "OtpFragment: subscribeObservers called")
    }


    private fun getOtpMessage(){
        Log.d("AppDebug", "getOtpMessage()")

        autoLogin.startSmsRetriver(object : AutoLogin.SmsCallback {
            override fun connectionFailed() {
                Log.d("AppDebug", "connectionFailed: OtpFragment")

                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun connectionSuccess(aVoid: Void) {
                Log.d("AppDebug", "connectionSuccess: OtpFragment")

                Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show()
            }

            override fun smsCallback(sms: String) {
                if(sms.contains("OTP") && sms.contains("brainonet")){
                    receivedOtp = sms.substring(0, 6)
                    val pattern = Pattern.compile("^[0-9]{1,6}\$")
                    val matcher = pattern.matcher(receivedOtp)
                    if(matcher.find()){
                        txt_otp.setText(receivedOtp)

                        Toast.makeText(activity, "OTP is successfully submitted.", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("AppDebug", "this is the message: {$receivedOtp}")
                }
            }

        })
    }


    override fun onStop() {
        super.onStop()
        autoLogin.stopSmsReciever()
    }
}



