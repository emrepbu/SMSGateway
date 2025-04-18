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
