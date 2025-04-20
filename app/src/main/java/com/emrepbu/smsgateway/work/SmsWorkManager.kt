package com.emrepbu.smsgateway.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * `SmsWorkManager` is a class responsible for managing scheduled background work related to SMS checks.
 * It utilizes the Android WorkManager library to enqueue and cancel periodic tasks.
 *
 * This class is designed to be used with Dependency Injection (e.g., Hilt) for easy instantiation and testing.
 *
 * @property context The application context, used to get an instance of the WorkManager.
 */
class SmsWorkManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleSmsCheckWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<PeriodicSmsCheckWorker>(
            1, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SMS_CHECK_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

    fun cancelSmsCheckWork() {
        workManager.cancelUniqueWork(PERIODIC_SMS_CHECK_WORK_NAME)
    }

    companion object {
        private const val PERIODIC_SMS_CHECK_WORK_NAME = "periodic_sms_check_work"
    }
}
