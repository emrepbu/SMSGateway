package com.emrepbu.smsgateway.domain.model

data class EmailConfig(
    val id: Long = 0,
    val smtpServer: String,
    val smtpPort: Int,
    val username: String,
    val password: String,
    val fromAddress: String,
    val useSsl: Boolean = true,
)
