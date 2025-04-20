package com.emrepbu.smsgateway.domain.model

/**
 * Represents a rule for filtering emails.
 *
 * This data class defines a set of criteria that can be used to match and filter incoming emails.
 * It includes options to filter by sender and message content, as well as to exclude emails based on certain criteria.
 *
 * @property id A unique identifier for the filter rule. Defaults to 0 if not specified.
 * @property name A descriptive name for the filter rule.
 * @property senderContains If specified, emails where the sender's address contains this string will match.
 *                          If null, this criterion is ignored.
 * @property messageContains If specified, emails where the message body contains this string will match.
 *                           If null, this criterion is ignored.
 * @property excludeSenderContains If specified, emails where the sender's address contains this string will be excluded.
 *                                 If null, this exclusion criterion is ignored.
 * @property excludeMessageContains If specified, emails where the message body contains this string will be excluded.
 *                                  If null, this exclusion criterion is ignored.
 * @property isEnabled Indicates whether the filter rule is currently active. Defaults to true.
 * @property emailAddresses A list of email addresses associated with this rule.
 * @property createdAt The timestamp (in milliseconds) when the filter rule was created. Defaults to the current time.
 */
data class FilterRule(
    val id: Long = 0,
    val name: String,
    val senderContains: String? = null,
    val messageContains: String? = null,
    val excludeSenderContains: String? = null,
    val excludeMessageContains: String? = null,
    val isEnabled: Boolean = true,
    val emailAddresses: List<String>,
    val createdAt: Long = System.currentTimeMillis(),
)
