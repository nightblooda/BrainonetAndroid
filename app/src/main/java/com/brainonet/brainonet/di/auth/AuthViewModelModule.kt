package com.brainonet.brainonet.di.auth

import androidx.lifecycle.ViewModel
import com.brainonet.brainonet.di.ViewModelKey
import com.brainonet.brainonet.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule{

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}