package com.brainonet.brainonet.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.brainonet.brainonet.R
import com.brainonet.brainonet.models.AccountProperties
import com.brainonet.brainonet.ui.main.MainActivity
import com.brainonet.brainonet.ui.main.account.state.AccountStateEvent
import com.brainonet.brainonet.util.MyFragmentPagerAdpater
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.item_tablayout_header.*
import kotlinx.android.synthetic.main.item_tablyout.*


class AccountFragment : BaseAccountFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()


        edit_profile.setOnClickListener{
            findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
        }

//        initToolbar()

// setupViewPager() is not functioning properly here so is put in subscribeOberservers()
        subscribeObservers()

    }

    private fun subscribeObservers(){

        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.let{
                it.data?.let{ data ->
                    data.data?.let{ event ->
                        event.getContentIfNotHandled()?.let{ viewState ->
                            viewState.accountProperties?.let{accountProperties ->
                                Log.d(TAG, "AccountFragment, DataState: ${accountProperties}")
                                viewModel.setAccountPropertiesData(accountProperties)
                            }
                        }
                    }

                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            if(viewState != null){
                viewState.accountProperties?.let{
                    Log.d(TAG, "AccountFragment, ViewState: ${it}")
                    setAccountDataFields(it)

                }
            }
            setupViewPager()

        })

    }

    override fun onResume() {
        super.onResume()
        viewModel.setStateEvent(
            AccountStateEvent.GetAccountPropertiesEvent()
        )
    }

    private fun setAccountDataFields(accountProperties: AccountProperties){
        karma?.setText(accountProperties.phoneNumber)
        tv_title?.setText(accountProperties.first_name + " " + accountProperties.last_name)
    }



    private fun setupViewPager() {
//        val adapter = ViewPagerAdapter((activity as MainActivity).supportFragmentManager)
        val adapter = MyFragmentPagerAdpater(childFragmentManager)
        adapter.addFragment(AccountAboutFragment(), "ABOUT")
        adapter.addFragment(AccountCommentFragment(), "COMMENTS")
        adapter.addFragment(AccountSavedFragment(), "SAVED")
        viepager_id.adapter = adapter
        tablayout_id.setupWithViewPager(viepager_id)
    }

//    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//        private val mFragmentList = ArrayList<Fragment>()
//        private val mFragmentTitleList = ArrayList<String>()
//
//        override fun getItem(position: Int): Fragment {
//            return mFragmentList[position]
//        }
//
//        override fun getCount(): Int {
//            return mFragmentList.size
//        }
//
//        fun addFragment(fragment: Fragment, title: String) {
//            if(mFragmentList.contains(fragment) || !mFragmentTitleList.contains(title)){
//                mFragmentList.add(fragment)
//                mFragmentTitleList.add(title)
//            }
//
//        }
//
//        override fun getPageTitle(position: Int): CharSequence {
//            return mFragmentTitleList[position]
//        }
//    }

//    private fun initToolbar() {
//        (activity as MainActivity).setSupportActionBar(toolbar)
//        val actionBar = (activity as MainActivity).supportActionBar
//        actionBar?.setHomeButtonEnabled(true)
//        actionBar?.title = "The title"
//        actionBar?.setDisplayHomeAsUpEnabled(true)
//    }




}