package com.emrepbu.smsgateway.domain.repository

import com.emrepbu.smsgateway.domain.model.SmsMessage
import kotlinx.coroutines.flow.Flow

interface SmsRepository {
    fun getAllSmsMessage(): Flow<List<SmsMessage>>
    fun getUnforwardedSmsMessages(): Flow<List<SmsMessage>>
    suspend fun getSmsById(id: String): SmsMessage?
    suspend fun insertMessage(sms: SmsMessage)
    suspend fun updateSmsForwardStatus(
        id: String,
        isForwarded: Boolean,
        forwardedTo: List<String>,
        forwardedAt: Long?
    )
    suspend fun refreshSmsFromSystem()
}
