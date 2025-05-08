package com.emrepbu.smsgateway.domain.usecase

import android.util.Log
import com.emrepbu.smsgateway.data.remote.api.model.ApiResult
import com.emrepbu.smsgateway.data.repository.ApiRepository
import com.emrepbu.smsgateway.domain.model.SmsMessage
import javax.inject.Inject

private const val TAG = "SendSmsToApiUseCase"

/**
 * [SendSmsToApiUseCase] is a use case responsible for sending SMS messages to an API.
 * 
 * It takes an SMS message and sends it to the configured API endpoint using the [ApiRepository].
 * The operation can succeed or fail based on network conditions, server response, etc.
 * 
 * @property apiRepository Repository for API operations.
 */
class SendSmsToApiUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    /**
     * Sends an SMS message to the API.
     * 
     * @param sms The SMS message to send.
     * @param senderName Optional sender name to include in the API request.
     * @return Result indicating success or failure with relevant information.
     */
    suspend operator fun invoke(sms: SmsMessage, senderName: String? = null): Result<Unit> {
        return try {
            val result = apiRepository.sendSms(
                phoneNumber = sms.sender,
                message = sms.message,
                senderName = senderName
            )
            
            when (result) {
                is ApiResult.Success -> {
                    Log.d(TAG, "SMS successfully sent to API: ${result.data.message}")
                    Result.success(Unit)
                }
                is ApiResult.Error -> {
                    Log.e(TAG, "Failed to send SMS to API: ${result.message}")
                    Result.failure(Exception("API error: ${result.message}"))
                }
                ApiResult.Loading -> {
                    // This state won't be returned from a suspend function
                    Result.failure(Exception("Unexpected loading state"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception sending SMS to API", e)
            Result.failure(e)
        }
    }
}
