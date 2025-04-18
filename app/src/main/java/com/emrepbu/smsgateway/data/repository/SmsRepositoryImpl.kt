package com.emrepbu.smsgateway.data.repository

import android.content.Context
import android.provider.Telephony
import com.emrepbu.smsgateway.data.local.dao.SmsDao
import com.emrepbu.smsgateway.data.local.entity.SmsMessageEntity
import com.emrepbu.smsgateway.domain.model.SmsMessage
import com.emrepbu.smsgateway.domain.repository.SmsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SmsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val smsDao: SmsDao,
) : SmsRepository {
    override fun getAllSmsMessage(): Flow<List<SmsMessage>> {
        return smsDao.gelAllSmsMessages().map {
            it.map(SmsMessageEntity::toDomainModel)
        }
    }

    override fun getUnforwardedSmsMessages(): Flow<List<SmsMessage>> {
        return smsDao.getUnforwardedSmsMessages().map {
            it.map(SmsMessageEntity::toDomainModel)
        }
    }

    override suspend fun getSmsById(id: String): SmsMessage? {
        return smsDao.getSmsById(id)?.toDomainModel()
    }

    override suspend fun insertMessage(sms: SmsMessage) {
        smsDao.insertSms(SmsMessageEntity.fromDomainModel(sms))
    }

    override suspend fun updateSmsForwardStatus(
        id: String,
        isForwarded: Boolean,
        forwardedTo: List<String>,
        forwardedAt: Long?,
    ) {
        smsDao.updateSmsForwardStatus(id, isForwarded, forwardedTo, forwardedAt)
    }

    override suspend fun refreshSmsFromSystem() = withContext(Dispatchers.IO) {
        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            null,
            null,
            Telephony.Sms.DEFAULT_SORT_ORDER,
        ) ?: return@withContext

        cursor.use {
            val idIndex = it.getColumnIndex(Telephony.Sms._ID)
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
            val typeIndex = it.getColumnIndex(Telephony.Sms.TYPE)
            val readIndex = it.getColumnIndex(Telephony.Sms.READ)

            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val address = it.getString(addressIndex) ?: "Unknown"
                val body = it.getString(bodyIndex) ?: ""
                val date = it.getLong(dateIndex)
                val type = it.getInt(typeIndex)
                val read = it.getInt(readIndex) == 1

                val existingSms = smsDao.getSmsById(id)

                if (existingSms == null) {
                    smsDao.insertSms(
                        SmsMessageEntity(
                            id = id,
                            sender = address,
                            message = body,
                            timestamp = date,
                            isRead = read,
                            type = type,
                            isForwarded = false,
                            forwardedTo = emptyList(),
                            forwardedAt = null,
                        )
                    )
                }
            }
        }
    }
}
