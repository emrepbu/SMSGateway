package com.emrepbu.smsgateway.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.emrepbu.smsgateway.domain.model.SmsMessage
import com.emrepbu.smsgateway.domain.repository.SmsRepository
import com.emrepbu.smsgateway.domain.usecase.ProcessNewSmsUseCase
import com.emrepbu.smsgateway.utils.AppEvents
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [ProcessSmsWorker] is a [CoroutineWorker] responsible for processing newly received SMS messages.
 *
 * It receives SMS data as input, persists it to the local database,
 * and then delegates further processing to the [ProcessNewSmsUseCase].
 * Finally, it notifies the application about a new SMS arrival through [AppEvents].
 *
 * This worker is designed to run in the background and handle the potentially
 * time-consuming tasks associated with processing SMS messages, such as database operations and
 * business logic processing, without blocking the main thread.
 *
 * @property context The application context.
 * @property params The worker parameters.
 * @property smsRepository Repository for managing SMS data in the database.
 * @property processNewSmsUseCase Use case for processing newly received SMS messages.
 */
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

            AppEvents.notifySmsReceived()

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