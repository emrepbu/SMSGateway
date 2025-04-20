package com.emrepbu.smsgateway.domain.model

/**
 * Represents a single SMS message.
 *
 * This data class encapsulates all the necessary information about an SMS message, including its
 * content, sender, timestamp, read status, type, and forwarding details.
 *
 * @property id The unique identifier of the SMS message.
 * @property sender The phone number or name of the message sender.
 * @property message The text content of the SMS message.
 * @property timestamp The timestamp (in milliseconds since the epoch) when the message was received or sent.
 * @property isRead Indicates whether the message has been read by the recipient. `true` if read, `false` otherwise.
 * @property type The type of the message. The specific meaning of the type is application-dependent (e.g., 1 for inbox, 2 for sent).
 * @property isForwarded Indicates whether the message has been forwarded. Defaults to `false`.
 * @property forwardedTo A list of phone numbers or contacts to whom the message was forwarded. Empty if not forwarded.
 * @property forwardedAt The timestamp (in milliseconds since the epoch) when the message was forwarded. `null` if not forwarded.
 */
data class SmsMessage(
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val type: Int,
    val isForwarded: Boolean = false,
    val forwardedTo: List<String> = emptyList(),
    val forwardedAt: Long? = null,
)
