package com.brainonet.brainonet.di.main

import androidx.lifecycle.ViewModel
import com.brainonet.brainonet.di.ViewModelKey
import com.brainonet.brainonet.ui.main.account.AccountViewModel
import com.brainonet.brainonet.ui.main.blog.viewmodel.BlogViewModel
import com.brainonet.brainonet.ui.main.community.viewmodel.CommunityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CommunityViewModel::class)
    abstract fun bindCommunityViewModel(communityViewModel: CommunityViewModel): ViewModel
}