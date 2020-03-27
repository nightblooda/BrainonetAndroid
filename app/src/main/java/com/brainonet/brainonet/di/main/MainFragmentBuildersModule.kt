package com.brainonet.brainonet.di.main

import com.brainonet.brainonet.ui.main.account.*
import com.brainonet.brainonet.ui.main.blog.BlogFragment
import com.brainonet.brainonet.ui.main.blog.ViewBlogFragment
import com.brainonet.brainonet.ui.main.community.CommunityFragment
import com.brainonet.brainonet.ui.main.community.ViewCommunityFragment
import com.brainonet.brainonet.ui.main.fake.FakeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeCommunityFragment(): CommunityFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewCommunityFragment(): ViewCommunityFragment

    @ContributesAndroidInjector()
    abstract fun contributeFakeFragment(): FakeFragment


    @ContributesAndroidInjector()
    abstract fun contributeFragmentAbout(): AccountAboutFragment

    @ContributesAndroidInjector()
    abstract fun contributeFragmentSaved(): AccountSavedFragment

    @ContributesAndroidInjector()
    abstract fun contributeFragmentComment(): AccountCommentFragment






}