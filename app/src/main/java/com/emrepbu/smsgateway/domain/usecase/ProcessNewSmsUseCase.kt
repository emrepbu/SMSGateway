package com.emrepbu.smsgateway.domain.usecase

import com.emrepbu.smsgateway.domain.model.SmsMessage
import com.emrepbu.smsgateway.domain.repository.FilterRuleRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * [ProcessNewSmsUseCase] is a use case class responsible for processing a newly received SMS message.
 *
 * It orchestrates the following steps:
 * 1. **Retrieving Enabled Filter Rules:** Fetches all enabled filter rules from the [FilterRuleRepository].
 * 2. **Applying Filter Rules:** Applies the fetched filter rules to the incoming SMS using the [ApplyFilterUseCase].
 * 3. **Determining Matches:** Checks if any filter rules matched the SMS content.
 * 4. **Extracting Email Addresses:** If matches are found, it extracts the associated email addresses from the matched rules.
 * 5. **Sending Email:** Sends the SMS content to the extracted email addresses using the [SendEmailForSmsUseCase].
 * 6. **Sending to API:** If API integration is enabled, sends the SMS to the configured API endpoint.
 * 7. **Returning Result:** Returns a [ForwardResult] indicating the success or failure of the process and provides relevant details.
 *
 * @property filterRuleRepository Repository for accessing filter rules.
 * @property applyFilterUseCase Use case for applying filter rules to SMS messages.
 * @property sendEmailForSmsUseCase Use case for sending an email containing the SMS content.
 * @property sendSmsToApiUseCase Use case for sending SMS to an API endpoint.
 * @property getApiConfigUseCase Use case for retrieving API configuration.
 */
class ProcessNewSmsUseCase @Inject constructor(
    private val filterRuleRepository: FilterRuleRepository,
    private val applyFilterUseCase: ApplyFilterUseCase,
    private val sendEmailForSmsUseCase: SendEmailForSmsUseCase,
    private val sendSmsToApiUseCase: SendSmsToApiUseCase,
    private val getApiConfigUseCase: GetApiConfigUseCase
) {
    suspend operator fun invoke(sms: SmsMessage): ForwardResult {
        // Email işlemi
        val enabledRules = filterRuleRepository.getEnabledFilterRules().first()
        val matches = applyFilterUseCase(sms, enabledRules)
        var emailResult = ForwardResult(false, emptyList(), "No filter rules matched")
        
        if (matches.isNotEmpty()) {
            val allEmailAddresses = matches.flatMap { it.rule.emailAddresses }.distinct()
            val result = sendEmailForSmsUseCase(sms, allEmailAddresses)

            emailResult = if (result.isSuccess) {
                ForwardResult(
                    true,
                    allEmailAddresses,
                    "SMS successfully delivered to ${allEmailAddresses.size} email address(es)"
                )
            } else {
                ForwardResult(
                    false,
                    emptyList(),
                    "Error sending email: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
                )
            }
        }
        
        // API işlemi
        val apiConfig = getApiConfigUseCase().first()
        var apiResult = ForwardResult(false, emptyList(), "API integration is disabled")
        
        if (apiConfig.enabled && apiConfig.apiUrl.isNotBlank()) {
            val result = sendSmsToApiUseCase(sms, apiConfig.customSenderName.takeIf { it.isNotBlank() })
            
            apiResult = if (result.isSuccess) {
                ForwardResult(
                    true,
                    emptyList(),
                    "SMS successfully sent to API"
                )
            } else {
                ForwardResult(
                    false,
                    emptyList(),
                    "Error sending to API: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
                )
            }
        }
        
        // Birleştirilmiş sonuç
        return when {
            emailResult.isSuccess && apiResult.isSuccess -> {
                ForwardResult(
                    true,
                    emailResult.emailsSentTo,
                    "SMS sent to both email and API successfully"
                )
            }
            emailResult.isSuccess -> emailResult
            apiResult.isSuccess -> apiResult
            else -> {
                ForwardResult(
                    false,
                    emptyList(),
                    "Failed to forward SMS: ${emailResult.message}, ${apiResult.message}"
                )
            }
        }
    }
}

/**
 * Represents the result of a forward operation, typically email forwarding.
 *
 * @property isSuccess Indicates whether the forwarding operation was successful.
 * @property emailsSentTo A list of email addresses to which the message was successfully forwarded.
 * @property message A descriptive message providing details about the outcome of the operation.
 *                    This message can contain success or failure details.
 */
data class ForwardResult(
    val isSuccess: Boolean,
    val emailsSentTo: List<String>,
    val message: String,
)
