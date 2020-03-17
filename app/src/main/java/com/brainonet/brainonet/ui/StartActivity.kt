package com.brainonet.brainonet.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.brainonet.brainonet.R
import com.brainonet.brainonet.ui.intro.IntroActivity


class StartActivity : AppCompatActivity() {

    /** Duration of wait  */
    private val SPLASH_DISPLAY_LENGTH = 5000  //splash screen will be shown for 2 seconds

    /** Called when the activity is first created.  */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.fragment_first)

        Handler().postDelayed({
            val mainIntent = Intent(this@StartActivity, IntroActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}