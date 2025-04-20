package com.emrepbu.smsgateway.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.emrepbu.smsgateway.domain.repository.SmsRepository
import com.emrepbu.smsgateway.domain.usecase.ProcessNewSmsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * [PeriodicSmsCheckWorker] is a worker that periodically checks for new SMS messages
 * in the system's SMS database and processes them.
 *
 * It performs the following actions:
 * 1. Refreshes the local SMS database with the latest SMS messages from the system.
 * 2. Retrieves a list of SMS messages that have not yet been forwarded.
 * 3. Processes each unforwarded SMS message using the [ProcessNewSmsUseCase].
 * 4. Logs the result of each SMS message processing.
 *
 * This worker is designed to be run periodically using WorkManager.
 *
 * @property context The application context.
 * @property params Worker parameters provided by WorkManager.
 * @property smsRepository The repository responsible for interacting with the SMS data.
 * @property processNewSmsUseCase The use case responsible for processing new SMS messages.
 */
@HiltWorker
class PeriodicSmsCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val smsRepository: SmsRepository,
    private val processNewSmsUseCase: ProcessNewSmsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Periodic SMS check started")


            smsRepository.refreshSmsFromSystem()

            val unforwardedMessages = smsRepository.getUnforwardedSmsMessages().first()

            Log.d(TAG, "${unforwardedMessages.size} unforwarded SMS found")

            unforwardedMessages.forEach { sms ->
                val result = processNewSmsUseCase(sms)
                Log.d(TAG, "SMS ${sms.id} processing result: ${result.message}")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "PeriodicSmsCheckWorker"
    }
}
