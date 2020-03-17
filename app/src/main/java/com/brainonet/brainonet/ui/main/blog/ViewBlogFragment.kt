package com.brainonet.brainonet.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.brainonet.brainonet.R
import com.brainonet.brainonet.models.BlogPost
import com.brainonet.brainonet.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_view_blog.*


class ViewBlogFragment : BaseBlogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        .setOnClickListener{
//            findNavController().navigate(R.id.action_viewBlogFragment_to_blogFragment)
//        }
//        toolbar.setNavigationOnClickListener {
//            findNavController().navigateUp()
//        }
        (activity as AppCompatActivity).supportActionBar?.hide()
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

//        toolbar.inflateMenu(R.menu.learn)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    // do something
                    Log.e(TAG, "sdflasdfk")
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
//        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
//        (activity as MainActivity).setSupportActionBar(toolbar)
//        setHasOptionsMenu(true)
//        stateChangeListener.expandAppBar()

        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.viewBlogFields.blogPost?.let { blogPost ->
                setBlogProperties(blogPost)
            }
        })
    }



    private fun setBlogProperties(blogPost: BlogPost){
        requestManager
            .load(blogPost.image)
            .into(image)
        course_subject.setText(blogPost.title)
        course_name.setText(blogPost.community)
        description.setText(blogPost.body)

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.learn, menu)
//
//    }
}