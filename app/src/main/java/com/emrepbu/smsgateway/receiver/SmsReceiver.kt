package com.emrepbu.smsgateway.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.WorkManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import com.emrepbu.smsgateway.utils.AppEvents
import com.emrepbu.smsgateway.work.ProcessSmsWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SmsReceiver is a [BroadcastReceiver] responsible for listening to incoming SMS messages.
 *
 * It intercepts the `android.provider.Telephony.SMS_RECEIVED` broadcast action,
 * extracts SMS details like sender, message body, and timestamp, and then enqueues
 * a [ProcessSmsWorker] to handle further processing of the SMS in a background thread.
 *
 * This receiver also notifies the application about the received SMS via [AppEvents].
 */
@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (sms in messages) {
            val sender = sms.originatingAddress ?: "Unknown"
            val body = sms.messageBody ?: ""
            val timestamp = System.currentTimeMillis()

            Log.d(TAG, "Received sms: $sender, $body")

            val inputData = Data.Builder()
                .putString(ProcessSmsWorker.KEY_SMS_ID, sms.indexOnIcc.toString())
                .putString(ProcessSmsWorker.KEY_SMS_SENDER, sender)
                .putString(ProcessSmsWorker.KEY_SMS_BODY, body)
                .putLong(ProcessSmsWorker.KEY_SMS_TIMESTAMP, timestamp)
                .build()
            println("Input data: $inputData")

            val processWorkRequest = OneTimeWorkRequestBuilder<ProcessSmsWorker>()
                .setInputData(inputData)
                .build()

            workManager.enqueue(processWorkRequest)

            CoroutineScope(Dispatchers.IO).launch {
                AppEvents.notifySmsReceived()
            }
        }
    }

    companion object {
        private const val TAG = "SmsReceiver"
    }
}
