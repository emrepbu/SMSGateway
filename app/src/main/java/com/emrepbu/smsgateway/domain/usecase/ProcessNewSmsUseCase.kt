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
 * 6. **Returning Result:** Returns a [ForwardResult] indicating the success or failure of the process and provides relevant details.
 *
 * @property filterRuleRepository Repository for accessing filter rules.
 * @property applyFilterUseCase Use case for applying filter rules to SMS messages.
 * @property sendEmailForSmsUseCase Use case for sending an email containing the SMS content.
 */
class ProcessNewSmsUseCase @Inject constructor(
    private val filterRuleRepository: FilterRuleRepository,
    private val applyFilterUseCase: ApplyFilterUseCase,
    private val sendEmailForSmsUseCase: SendEmailForSmsUseCase,
) {
    suspend operator fun invoke(sms: SmsMessage): ForwardResult {
        val enabledRules = filterRuleRepository.getEnabledFilterRules().first()

        val matches = applyFilterUseCase(sms, enabledRules)

        if (matches.isEmpty()) {
            return ForwardResult(false, emptyList(), "No filter rules matched")
        }

        val allEmailAddresses = matches.flatMap { it.rule.emailAddresses }.distinct()

        val result = sendEmailForSmsUseCase(sms, allEmailAddresses)

        return if (result.isSuccess) {
            ForwardResult(
                true,
                allEmailAddresses,
                "SMS successfully delivered to email address ${allEmailAddresses.size}"
            )
        } else {
            ForwardResult(
                false,
                emptyList(),
                "Error sending email: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
            )
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
    val emailsSentT: List<String>,
    val message: String,
)