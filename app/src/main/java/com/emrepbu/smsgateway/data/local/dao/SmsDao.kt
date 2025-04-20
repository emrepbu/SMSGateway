package com.emrepbu.smsgateway.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emrepbu.smsgateway.data.local.entity.SmsMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsDao {
    @Query("SELECT * FROM sms_messages ORDER BY timestamp DESC")
    fun gelAllSmsMessages(): Flow<List<SmsMessageEntity>>

    @Query("SELECT * FROM sms_messages WHERE isForwarded = 0 ORDER BY timestamp DESC")
    fun getUnforwardedSmsMessages(): Flow<List<SmsMessageEntity>>

    @Query("SELECT * FROM sms_messages WHERE id = :id")
    suspend fun getSmsById(id: String): SmsMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSms(sms: SmsMessageEntity)

    @Update
    suspend fun updateSms(sms: SmsMessageEntity)

    @Query("UPDATE sms_messages SET isForwarded = :isForwarded, forwardedTo = :forwardedTo, forwardedAt = :forwardedAt WHERE id = :id")
    suspend fun updateSmsForwardStatus(
        id: String,
        isForwarded: Boolean,
        forwardedTo: List<String>,
        forwardedAt: Long?
    )
}
