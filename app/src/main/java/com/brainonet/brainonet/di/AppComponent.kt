package com.brainonet.brainonet.di

import android.app.Application
import com.brainonet.brainonet.BaseApplication
import com.brainonet.brainonet.session.SessionManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    ActivityBuilderModule::class,
    ViewModelFactoryModule::class
    ]
)
interface AppComponent: AndroidInjector<BaseApplication>{

    val sessionManager: SessionManager //must be add here because injecting into classes becomes easier(abstract)

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}