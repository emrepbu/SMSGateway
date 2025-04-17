package com.emrepbu.smsgateway.domain.model

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
