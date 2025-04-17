package com.emrepbu.smsgateway.domain.usecase

import com.emrepbu.smsgateway.domain.model.SmsMessage
import com.emrepbu.smsgateway.domain.repository.EmailRepository
import com.emrepbu.smsgateway.domain.repository.SmsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Use case responsible for forwarding SMS messages to specified email addresses.
 * It handles email creation, sending, and updating SMS forward status.
 */
class SendEmailForSmsUseCase @Inject constructor(
    private val emailRepository: EmailRepository,
    private val smsRepository: SmsRepository,
) {
    /**
     * Forwards an SMS message to the specified email addresses.
     *
     * @param sms The SMS message to be forwarded
     * @param emailAddresses List of recipient email addresses
     * @return Result indicating success or failure of the operation
     */
    suspend operator fun invoke(
        sms: SmsMessage,
        emailAddresses: List<String>,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val emailConfig = emailRepository.getEmailConfig()
                ?: return@withContext Result.failure(Exception("Email configuration not found"))

            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(sms.timestamp))

            val subject = "SMS forwarding: ${sms.sender}"
            val body = """
            |Sender: ${sms.sender}
            |Date: $formattedDate
            |
            |Message:
            |${sms.message}
            |
            |This email was automatically sent by the SMS Reader app.
            |""".trimMargin()

            val result = emailRepository.sendEmail(
                to = emailAddresses,
                subject = subject,
                body = body,
                config = emailConfig,
            )

            // If email was sent successfully, update the SMS status in the database
            if (result.isSuccess) {
                smsRepository.updateSmsForwardStatus(
                    id = sms.id,
                    isForwarded = true,
                    forwardedTo = emailAddresses,
                    forwardedAt = System.currentTimeMillis()
                )
            }

            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
