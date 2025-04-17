package com.emrepbu.smsgateway.domain.usecase

import com.emrepbu.smsgateway.domain.model.FilterRule
import com.emrepbu.smsgateway.domain.model.SmsMessage
import javax.inject.Inject

/**
 * Use case responsible for applying filter rules to SMS messages.
 * Returns a list of matches between SMS messages and filter rules.
 */
class ApplyFilterUseCase @Inject constructor() {
    operator fun invoke(
        sms: SmsMessage,
        rules: List<FilterRule>,
    ): List<FilterMatch> {
        return rules.mapNotNull { rule ->
            // Skip disabled rules
            if (!rule.isEnabled) return@mapNotNull null

            // Check if the SMS sender contains specified text (or if this condition is empty/null)
            val senderMatch = rule.senderContains.isNullOrEmpty() ||
                    sms.sender.contains(rule.senderContains, ignoreCase = true)

            // Check if the SMS sender does NOT contain excluded text (or if this condition is empty/null)
            val senderExcludeMatch = rule.excludeSenderContains.isNullOrEmpty() ||
                    !sms.sender.contains(rule.excludeSenderContains, ignoreCase = true)

            // Check if the SMS message contains specified text (or if this condition is empty/null)
            val messageMatch = rule.messageContains.isNullOrEmpty() ||
                    sms.message.contains(rule.messageContains, ignoreCase = true)

            // Check if the SMS message does NOT contain excluded text (or if this condition is empty/null)
            val messageExcludeMatch = rule.excludeMessageContains.isNullOrEmpty() ||
                    !sms.message.contains(rule.excludeMessageContains, ignoreCase = true)

            // If all conditions are met, create a match between this rule and the SMS
            if (senderMatch &&
                senderExcludeMatch &&
                messageMatch &&
                messageExcludeMatch
            ) {
                FilterMatch(rule, sms)
            } else {
                null
            }
        }
    }
}

/**
 * Data class representing a match between a filter rule and an SMS message.
 */
data class FilterMatch(
    val rule: FilterRule,
    val sms: SmsMessage,
)
