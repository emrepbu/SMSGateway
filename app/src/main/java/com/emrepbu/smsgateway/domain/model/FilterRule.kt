package com.emrepbu.smsgateway.domain.model

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
