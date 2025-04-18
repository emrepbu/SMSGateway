package com.example.smsgateway.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.emrepbu.smsgateway.domain.model.SmsMessage
import com.emrepbu.smsgateway.domain.repository.SmsRepository
import com.emrepbu.smsgateway.domain.usecase.ProcessNewSmsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ProcessSmsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val smsRepository: SmsRepository,
    private val processNewSmsUseCase: ProcessNewSmsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val smsId = inputData.getString(KEY_SMS_ID)
                ?: return@withContext Result.failure()
            val sender = inputData.getString(KEY_SMS_SENDER)
                ?: "Unknown"
            val body = inputData.getString(KEY_SMS_BODY)
                ?: ""
            val timestamp = inputData.getLong(KEY_SMS_TIMESTAMP, System.currentTimeMillis())

            val sms = SmsMessage(
                id = smsId,
                sender = sender,
                message = body,
                timestamp = timestamp,
                isRead = false,
                type = 1,
                isForwarded = false
            )

            smsRepository.insertMessage(sms)

            val result = processNewSmsUseCase(sms)

            Log.d(TAG, "SMS Processing Result: ${result.message}")

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error occurred while processing SMS", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "ProcessSmsWorker"

        const val KEY_SMS_ID = "key_sms_id"
        const val KEY_SMS_SENDER = "key_sms_sender"
        const val KEY_SMS_BODY = "key_sms_body"
        const val KEY_SMS_TIMESTAMP = "key_sms_timestamp"
    }
}