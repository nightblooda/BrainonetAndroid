package com.brainonet.brainonet.ui.auth

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.android.synthetic.main.fragment_mobile.*

class AutoLogin(context: Context){
    private var smsCallback: SmsCallback? = null
    private val context: Context
    private var smsBroadcastReceiver: BroadcastReceiver? = null
    private val appCompatActivity: AppCompatActivity = context as AppCompatActivity
    private var intentFilter: IntentFilter? = null

    init{
        this.context = appCompatActivity.applicationContext
    }


    private val CREDENTIAL_PICKER_REQUEST = 1  // Set to an unused request code

    //     Construct a request for phone numbers and show the picker
    fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val credentialsClient = Credentials.getClient(context)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        try {
            appCompatActivity.startIntentSenderForResult(
                intent.intentSender,
                CREDENTIAL_PICKER_REQUEST,
                null, 0, 0, 0
            )
        } catch(e: IntentSender.SendIntentException){
            Log.e("PHONE_HINT", "Error in picking moblie number")
        }
    }

    fun startSmsRetriver(smsCallback: SmsCallback){
        registerReceiver()
        this.smsCallback = smsCallback
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        val client = SmsRetriever.getClient(context)

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        val task = client.startSmsRetriever()
        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener { aVoid ->
            Log.d("AppDebug", "task.addOnSuccessListener")
            if(aVoid!=null){
                smsCallback.connectionSuccess(aVoid)
            }
        }

        task.addOnFailureListener {
            Log.d("AppDebug", "task.addOnFailureListener")
            smsCallback.connectionFailed()
        }

    }

    fun getPhoneNo(data: Intent): String{
        val cred = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
        return cred.id
    }


    private fun registerReceiver(){
        //filter to receive SMS
        intentFilter = IntentFilter()
        intentFilter!!.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

        //receiver to receive and to get otp from SMS
        smsBroadcastReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent) {
                if(SmsRetriever.SMS_RETRIEVED_ACTION == intent.action){
                    val extras = intent.extras
                    val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status?
                    when(status!!.statusCode){
                        CommonStatusCodes.SUCCESS -> {
                            //Get SMS message contents
                            Log.d("AppDebug", "broadcastReaceiver: successfull reterive")
                            val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String?
                            //Extract otp from the message and complete varification
                            //by sending the code back to server for SMS authenticity.
                            Log.d("AppDebug", "broadcastReaceiver: ${message}")
                            smsCallback!!.smsCallback(message.toString())
                            stopSmsReciever()
                        }
                        CommonStatusCodes.TIMEOUT -> {
                            //waiting for SMS timed out(5 minutes)
                            smsCallback!!.connectionFailed()
                            Log.d("AppDebug", "broadcastReceiver: Failed timeout")
                        }
                    }
                }
            }
        }

       appCompatActivity.application.registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    fun stopSmsReciever() {
        try {
            appCompatActivity.getApplicationContext().unregisterReceiver(smsBroadcastReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }


    interface SmsCallback{
        fun connectionFailed()
        fun connectionSuccess(aVoid: Void)
        fun smsCallback(sms: String)
    }


}