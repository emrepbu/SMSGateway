package com.emrepbu.smsgateway.di

import com.emrepbu.smsgateway.data.remote.api.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiService()
    }
}
