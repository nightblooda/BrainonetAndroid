package com.brainonet.brainonet.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.brainonet.brainonet.R
import com.brainonet.brainonet.ui.BaseActivity
import com.brainonet.brainonet.ui.auth.AuthActivity
import com.brainonet.brainonet.ui.main.account.AccountCommentFragment
import com.brainonet.brainonet.ui.main.account.AccountSavedFragment
import com.brainonet.brainonet.ui.main.account.BaseAccountFragment
import com.brainonet.brainonet.ui.main.account.UpdateAccountFragment
import com.brainonet.brainonet.ui.main.blog.BaseBlogFragment
import com.brainonet.brainonet.ui.main.blog.ViewBlogFragment
import com.brainonet.brainonet.ui.main.community.state.BaseCommunityFragment
import com.brainonet.brainonet.ui.main.fake.BaseFakeFragment
import com.brainonet.brainonet.util.BottomNavController
import com.brainonet.brainonet.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_blog.*
import kotlinx.android.synthetic.main.fragment_update_account.*
import kotlinx.android.synthetic.main.activity_main.progress_bar as progress_bar1

class MainActivity : BaseActivity(),
    BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener
{

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE){
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this
        )
    }

    override fun getNavGraphId(itemId: Int) = when(itemId){
        R.id.nav_blog -> {
            R.navigation.blog_nav_graph
        }
        R.id.nav_community -> {
            R.navigation.community_nav_graph
        }
        R.id.nav_fake -> {
            R.navigation.fake_nav_graph
        }
        R.id.nav_account -> {
            R.navigation.account_nav_graph
        }
        else -> {
            R.navigation.blog_nav_graph
        }
    }

    override fun onGraphChange() {
        cancelActiveJobs()
        expandAppBar()
    }

    // (1)whenever switching to different graph then cancel the active jobs of previous graph.
    private fun cancelActiveJobs(){

        val fragments = bottomNavController.fragmentManager
            .findFragmentById(bottomNavController.containerId)
            ?.childFragmentManager
            ?.fragments
        if(fragments != null){
            for(fragment in fragments){
                when(fragment){
                    is BaseAccountFragment -> fragment.cancelActiveJobs()
                    is BaseBlogFragment -> fragment.cancelActiveJobs()
                    is BaseCommunityFragment -> fragment.cancelActiveJobs()
                    is BaseFakeFragment -> fragment.cancelActiveJobs()

                }
            }
        }
        displayProgressBar(false)
    }

    override fun onReselectNavItem(
        navController: NavController,
        fragment: Fragment
    ) = when(fragment){

        is ViewBlogFragment -> {
            navController.navigate(R.id.action_viewBlogFragment_to_blogFragment)
        }


        is UpdateAccountFragment -> {
            navController.navigate(R.id.action_updateAccountFragment_to_accountFragment)
        }


        else -> {
            // do nothing
        }
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()



    override fun displayProgressBar(bool: Boolean) {
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if(savedInstanceState == null){
            bottomNavController.onNavigationItemSelected()
        }

        subscribeObservers()

//        logout_btn.setOnClickListener {
//            sessionManager.logout()
//        }

    }



    fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer{authToken ->
            Log.d(TAG, "MainActivity, subscribeObservers: ViewState: ${authToken}")
            if(authToken == null || authToken.account_pk == -1 || authToken.token == null){
                navAuthActivity()
                finish()
            }
        })
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }

    private fun setupActionBar(){
        setSupportActionBar(tool_bar)
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }


}
