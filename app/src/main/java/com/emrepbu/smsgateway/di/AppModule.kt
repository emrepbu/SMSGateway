package com.emrepbu.smsgateway.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.work.WorkManager
import com.emrepbu.smsgateway.data.local.dao.FilterRuleDao
import com.emrepbu.smsgateway.data.local.dao.SmsDao
import com.emrepbu.smsgateway.data.local.database.SmsDatabase
import com.emrepbu.smsgateway.data.local.datastore.EmailConfigDataStore
import com.emrepbu.smsgateway.data.remote.email.EmailService
import com.emrepbu.smsgateway.data.repository.EmailRepositoryImpl
import com.emrepbu.smsgateway.data.repository.FilterRuleRepositoryImpl
import com.emrepbu.smsgateway.data.repository.SmsRepositoryImpl
import com.emrepbu.smsgateway.domain.repository.EmailRepository
import com.emrepbu.smsgateway.domain.repository.FilterRuleRepository
import com.emrepbu.smsgateway.domain.repository.SmsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.annotation.Signed
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): SmsDatabase {
        return Room.databaseBuilder(
            context,
            SmsDatabase::class.java,
            "sms_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSmsDao(
        database: SmsDatabase,
    ): SmsDao {
        return database.smsDao()
    }

    @Provides
    @Singleton
    fun provideFilterRuleDao(
        database: SmsDatabase,
    ): FilterRuleDao {
        return database.filterRuleDao()
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context,
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSmsRepository(
        smsDao: SmsDao,
        @ApplicationContext context: Context,
    ): SmsRepository {
        return SmsRepositoryImpl(context, smsDao)
    }

    @Provides
    @Singleton
    fun provideFilterRuleRepository(
        filterRuleDao: FilterRuleDao,
    ): FilterRuleRepository {
        return FilterRuleRepositoryImpl(filterRuleDao)
    }

    @Provides
    @Singleton
    fun provideEmailRepository(
        emailConfigDataStore: EmailConfigDataStore,
        emailService: EmailService,
    ): EmailRepository {
        return EmailRepositoryImpl(emailConfigDataStore, emailService)
    }
}
