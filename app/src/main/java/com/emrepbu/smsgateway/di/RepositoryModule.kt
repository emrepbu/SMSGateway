package com.emrepbu.smsgateway.di

import com.emrepbu.smsgateway.data.repository.ApiConfigRepositoryImpl
import com.emrepbu.smsgateway.domain.repository.ApiConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindConfigRepository(
        configRepositoryImpl: ApiConfigRepositoryImpl
    ): ApiConfigRepository
}
