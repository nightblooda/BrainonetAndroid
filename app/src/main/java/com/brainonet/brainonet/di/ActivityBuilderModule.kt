package com.brainonet.brainonet.di

import com.brainonet.brainonet.di.auth.AuthFragmentBuildersModule
import com.brainonet.brainonet.di.auth.AuthModule
import com.brainonet.brainonet.di.auth.AuthScope
import com.brainonet.brainonet.di.auth.AuthViewModelModule
import com.brainonet.brainonet.di.main.MainFragmentBuildersModule
import com.brainonet.brainonet.di.main.MainModule
import com.brainonet.brainonet.di.main.MainScope
import com.brainonet.brainonet.di.main.MainViewModelModule
import com.brainonet.brainonet.ui.main.MainActivity
import com.brainonet.brainonet.ui.auth.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule{

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity
}