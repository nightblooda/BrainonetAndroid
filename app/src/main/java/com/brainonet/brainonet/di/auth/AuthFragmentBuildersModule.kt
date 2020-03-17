package com.brainonet.brainonet.di.auth

import com.brainonet.brainonet.ui.auth.MobileFragment
import com.brainonet.brainonet.ui.auth.OtpFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule{

    @ContributesAndroidInjector()
    abstract fun contributeMobileFragment(): MobileFragment

    @ContributesAndroidInjector()
    abstract fun contributeOtpFragment(): OtpFragment

}