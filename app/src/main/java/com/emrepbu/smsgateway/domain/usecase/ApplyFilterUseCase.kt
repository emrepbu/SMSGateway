package com.emrepbu.smsgateway.domain.usecase

import com.emrepbu.smsgateway.domain.model.FilterRule
import com.emrepbu.smsgateway.domain.model.SmsMessage
import javax.inject.Inject

/**
 * `ApplyFilterUseCase` is a use case class responsible for applying a list of [FilterRule]s to an [SmsMessage].
 * It iterates through each rule and checks if the SMS message matches the criteria defined in the rule.
 *
 * This class determines whether a given SMS message should be considered a match based on the enabled rules.
 * It checks for both inclusion and exclusion criteria for sender and message content.
 *
 * @constructor Creates an instance of [ApplyFilterUseCase]. No external dependencies are needed.
 */
class ApplyFilterUseCase @Inject constructor() {
    operator fun invoke(
        sms: SmsMessage,
        rules: List<FilterRule>,
    ): List<FilterMatch> {
        return rules.mapNotNull { rule ->
            if (!rule.isEnabled) return@mapNotNull null

            val senderMatch = rule.senderContains.isNullOrEmpty() ||
                    sms.sender.contains(rule.senderContains, ignoreCase = true)

            val senderExcludeMatch = rule.excludeSenderContains.isNullOrEmpty() ||
                    !sms.sender.contains(rule.excludeSenderContains, ignoreCase = true)

            val messageMatch = rule.messageContains.isNullOrEmpty() ||
                    sms.message.contains(rule.messageContains, ignoreCase = true)

            val messageExcludeMatch = rule.excludeMessageContains.isNullOrEmpty() ||
                    !sms.message.contains(rule.excludeMessageContains, ignoreCase = true)

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
 * Represents a match between an [SmsMessage] and a [FilterRule].
 *
 * This data class encapsulates the successful identification of an SMS message
 * that meets the criteria defined by a specific filter rule. It provides access
 * to both the rule that was matched and the SMS message itself.
 *
 * @property rule The [FilterRule] that matched the SMS message. This allows
 *                inspection of the criteria that led to the match.
 * @property sms The [SmsMessage] that was matched against the filter rule.
 *               This provides the details of the SMS message that satisfied
 *               the filter's conditions.
 */
data class FilterMatch(
    val rule: FilterRule,
    val sms: SmsMessage,
)
