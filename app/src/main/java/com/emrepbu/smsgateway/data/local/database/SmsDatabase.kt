package com.emrepbu.smsgateway.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emrepbu.smsgateway.data.local.converter.StringListConverter
import com.emrepbu.smsgateway.data.local.dao.FilterRuleDao
import com.emrepbu.smsgateway.data.local.dao.SmsDao
import com.emrepbu.smsgateway.data.local.entity.FilterRuleEntity
import com.emrepbu.smsgateway.data.local.entity.SmsMessageEntity

@Database(
    entities = [
        SmsMessageEntity::class,
        FilterRuleEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    value = [
        StringListConverter::class,
    ]
)
abstract class SmsDatabase : RoomDatabase() {
    abstract fun smsDao(): SmsDao
    abstract fun filterRuleDao(): FilterRuleDao
}
