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
 * Use case responsible for sending an email notification for an SMS message.
 *
 * This class encapsulates the logic for formatting an SMS message into an email,
 * retrieving email configuration, sending the email, and updating the SMS message's
 * forwarding status in the repository.
 *
 * @property emailRepository The repository for handling email-related operations.
 * @property smsRepository The repository for handling SMS-related operations.
 */
class SendEmailForSmsUseCase @Inject constructor(
    private val emailRepository: EmailRepository,
    private val smsRepository: SmsRepository,
) {
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
            |This email was automatically sent by the SMS Gateway app.
            |""".trimMargin()

            val result = emailRepository.sendEmail(
                to = emailAddresses,
                subject = subject,
                body = body,
                config = emailConfig,
            )

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
