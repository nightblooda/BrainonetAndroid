package com.brainonet.brainonet.di

import androidx.lifecycle.ViewModelProvider
import com.brainonet.brainonet.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule{

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}