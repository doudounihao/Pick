package com.app.qqwpick.di

import com.app.qqwpick.net.NetApi
import com.app.qqwpick.net.RetrofitManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetWorkModule {

    @Singleton
    @Provides
    fun provideNetApi(): NetApi {
        return RetrofitManager.instance.create(NetApi::class.java)
    }
}
