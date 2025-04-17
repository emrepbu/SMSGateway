package com.emrepbu.smsgateway.domain.usecase

import com.emrepbu.smsgateway.domain.model.SmsMessage
import com.emrepbu.smsgateway.domain.repository.FilterRuleRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Main use case that orchestrates the entire SMS processing workflow:
 * 1. Retrieves enabled filter rules
 * 2. Applies filters to the SMS message
 * 3. If matches are found, forwards the SMS to email addresses
 * 4. Returns the result of the operation
 */
class ProcessNewSmsUseCase @Inject constructor(
    private val filterRuleRepository: FilterRuleRepository,
    private val applyFilterUseCase: ApplyFilterUseCase,
    private val sendEmailForSmsUseCase: SendEmailForSmsUseCase,
) {
    /**
     * Processes a new SMS message by filtering and forwarding it if necessary.
     *
     * @param sms The SMS message to process
     * @return ForwardResult indicating success or failure and additional details
     */
    suspend operator fun invoke(sms: SmsMessage): ForwardResult {
        // Get all enabled filter rules from the repository
        val enabledRules = filterRuleRepository.getEnabledFilterRules().first()

        // Apply filter rules to the SMS message to find matches
        val matches = applyFilterUseCase(sms, enabledRules)

        // If no filter rules matched, return early with failure result
        if (matches.isEmpty()) {
            return ForwardResult(false, emptyList(), "No filter rules matched")
        }

        // Collect all unique email addresses from matched rules
        val allEmailAddresses = matches.flatMap { it.rule.emailAddresses }.distinct()

        // Forward the SMS to all collected email addresses
        val result = sendEmailForSmsUseCase(sms, allEmailAddresses)

        // Return appropriate result based on email forwarding success/failure
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

    /**
     * Data class representing the result of the SMS forwarding operation.
     *
     * @property isSuccess Whether the operation was successful
     * @property emailsSentT List of email addresses the SMS was sent to
     * @property message Human-readable message describing the result
     */
    data class ForwardResult(
        val isSuccess: Boolean,
        val emailsSentT: List<String>,
        val message: String,
    )
}