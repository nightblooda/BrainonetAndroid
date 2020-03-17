package com.brainonet.brainonet.ui.main.blog

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.brainonet.brainonet.R
import com.brainonet.brainonet.models.BlogPost
import com.brainonet.brainonet.ui.DataState
import com.brainonet.brainonet.ui.main.blog.state.BlogStateEvent
import com.brainonet.brainonet.ui.main.blog.state.BlogViewState
import com.brainonet.brainonet.ui.main.blog.viewmodel.*
import com.brainonet.brainonet.util.ErrorHandling
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject

class BlogFragment : BaseBlogFragment(),
    BlogListAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener

{

    private lateinit var recyclerAdapter: BlogListAdapter

    private lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setLogo(R.drawable.ic_brainonet_small)

        swipe_refresh.setOnRefreshListener(this)

//        fab_switcher.setOnClickListener{
//            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
//        }

        initRecycleView()
        subscribeObservers()

        if(savedInstanceState == null){
            viewModel.loadFirstPage()
        }
    }

    private fun onBlogSearchOrFilter(){
        viewModel.loadFirstPage().let{
            resetUI()
        }
    }


    private fun resetUI(){
        news_rv.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
    }

    //    private fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity){
//        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
//        NavigationUI.setupActionBarWithNavController(
//            activity,
//            findNavController(),
//            appBarConfiguration
//        )
//    }

//    private fun executeSearch(){
//        viewModel.setQuery("")
//        viewModel.setStateEvent(
//            BlogStateEvent.BlogSearchEvent()
//        )
//    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if(dataState != null){
                // calling handlePagination before onDataStateChange() will consume the event
                // if there is error and will not be displayed on ui
                handlePagination(dataState)
                stateChangeListener.onDataStateChange(dataState)

            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{viewState ->
            Log.d(TAG, "BlogFragment, ViewState: ${viewState}")
            if(viewState != null){
                recyclerAdapter.submitList(
                    list = viewState.blogFields.blogList,
                    isQueryExhausted = viewState.blogFields.isQueryExhausted
                )
            }
        })
    }

    private fun initSearchView(menu: Menu){
        activity?.apply{
            val searchManager:SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        // case 1: Enter on computer keyboard or arrow on virtual keyboard
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener{ v, actionId, event ->

            if(actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH){

                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...$searchQuery")
                viewModel.setQuery(searchQuery).let{
                    onBlogSearchOrFilter()
                }
            }
            true
        }

        // case 2: Search button clicked (in toolbar)
        (searchView.findViewById(R.id.search_go_btn) as View).setOnClickListener{
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...$searchQuery")
            viewModel.setQuery(searchQuery).let{
                onBlogSearchOrFilter()
            }
        }
    }

    private fun handlePagination(dataState: DataState<BlogViewState>){

        // handle incoming data from DataState
        dataState.data?.let{
            it.data?.let{
                it.getContentIfNotHandled()?.let{
                    viewModel.handleIncomingBlogListData(it)
                }
            }
        }

        // Check fo pagination end (ex: "no more result")
        // must do this b/c server will return ApiErrorResponse if page is not valid
        // -> Meaning there is no more data!
        dataState.error?.let{ event ->
            event.peekContent().response.message?.let{
                if(ErrorHandling.isPaginationDone(it)){

                    // handle the error message event so it doesn't play on ui
                    event.getContentIfNotHandled()

                    // set query exhausted to update recyclerview with
                    // "No more results..." list item
                    viewModel.setQueryExhausted(true)
                }
            }
        }
    }

    private fun initRecycleView(){
        news_rv.apply{
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            // add item decoration

            recyclerAdapter = BlogListAdapter(
                requestManager = requestManager,
                interaction = this@BlogFragment
            )

            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if(lastPosition == recyclerAdapter.itemCount.minus(1)){
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
                        viewModel.nextPage()
                    }
                }
            })
            adapter = recyclerAdapter


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references can leak memory
        news_rv.adapter = null
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        Log.d(TAG, "onItemSelected: position: ${position}, item: $item")
        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    override fun onRefresh() {
        onBlogSearchOrFilter()
        swipe_refresh.isRefreshing = false

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search, menu)
        initSearchView(menu)
    }


}