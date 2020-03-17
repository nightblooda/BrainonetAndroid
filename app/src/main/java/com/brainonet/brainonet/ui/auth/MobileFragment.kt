package com.brainonet.brainonet.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import kotlinx.android.synthetic.main.fragment_mobile.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.brainonet.brainonet.R
import com.brainonet.brainonet.models.AuthToken
import com.brainonet.brainonet.ui.auth.state.AuthStateEvent
import com.brainonet.brainonet.util.ApiEmptyResponse
import com.brainonet.brainonet.util.ApiErrorResponse
import com.brainonet.brainonet.util.ApiSuccessResponse


class MobileFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        Log.d(TAG, "MobileFragment: ${viewModel.hashCode()}")

        subscribeObservers()


        otp_btn.setOnClickListener {
            otpGenerate()
        }

//        otp_btn.setOnClickListener {
//            if (mobile_number.length() == 0){
//                Toast.makeText(activity, "Kindly enter your mobile number", Toast.LENGTH_SHORT).show()
//            } else if (mobile_number.length() != 10){
//                Toast.makeText(activity, "Kindly enter valid moblie number.", Toast.LENGTH_SHORT).show()
//            } else {
//                viewModel.testLogin().observe(viewLifecycleOwner, Observer { response ->
//                    when (response) {
//
//                        is ApiSuccessResponse -> {
//                            Log.d(TAG, "Login Response: ${response.body}")
//                            openFragmentOtp()
//                        }
//
//                        is ApiErrorResponse -> {
//                            Log.d(TAG, "Login Response Error: ${response.errorMessage}")
//
//                        }
//
//                        is ApiEmptyResponse -> {
//                            Log.d(TAG, "Login Response: Empty Response")
//
//                        }
//                    }
//                })
//            }
//        }

        mobile_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (mobile_number.length() == 1) {
                    if (mobile_number.text.toString().startsWith("6") ||
                        mobile_number.text.toString().startsWith("7") ||
                        mobile_number.text.toString().startsWith("8") ||
                        mobile_number.text.toString().startsWith("9")
                    ) {
                    } else {
                        mobile_number.setText("")
                        mobile_number.error = "Enter valid mobile number"
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val word = "We will send you an OTP to the number"
                display_text.text = "$word ${"+91 "+s.toString()}"
            }

        })


    }

    fun otpGenerate() {
//        openFragmentOtp()
        viewModel.setStateEvent(
            AuthStateEvent.OTPGenerateAttemptEvent(
                mobile_number.text.toString()
            )
        )
    }

    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer{viewState ->
            viewState.otpGenerationFields?.let{
                it.phoneNumber?.let{mobile_number.setText(it)}
            }
            Log.d(TAG, "MobileFragment: SubscribeObservers")
            viewState.otpCheck?.let {
                Log.d(TAG, "otpCheck: worked")
                if(it){
                    viewState.otpCheck = false

                    openFragmentOtp(mobile_number.text.toString())
                }
            }
        })
    }

    private fun openFragmentOtp(num: String){
        val args = Bundle()
        args.putString("phone_number", num)
        findNavController().navigate(R.id.action_fragmentMobile_to_fragmentOtp, args)
    }
}










