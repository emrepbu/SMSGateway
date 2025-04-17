package com.emrepbu.smsgateway.domain.repository

import com.emrepbu.smsgateway.domain.model.EmailConfig

interface EmailRepository {
    suspend fun getEmailConfig(): EmailConfig?
    suspend fun saveEmailConfig(config: EmailConfig)
    suspend fun sendEmail(
        to: List<String>,
        subject: String,
        body: String,
        config: EmailConfig? = null,
    ): Result<Unit>
}
