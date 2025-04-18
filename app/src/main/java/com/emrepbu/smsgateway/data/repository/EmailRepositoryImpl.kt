package com.emrepbu.smsgateway.data.repository

import com.emrepbu.smsgateway.data.local.datastore.EmailConfigDataStore
import com.emrepbu.smsgateway.data.remote.email.EmailService
import com.emrepbu.smsgateway.domain.model.EmailConfig
import com.emrepbu.smsgateway.domain.repository.EmailRepository
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val emailConfigDataStore: EmailConfigDataStore,
    private val emailService: EmailService,
) : EmailRepository {
    override suspend fun getEmailConfig(): EmailConfig? {
        return emailConfigDataStore.getEmailConfig()
    }

    override suspend fun saveEmailConfig(config: EmailConfig) {
        emailConfigDataStore.saveEmailConfig(config)
    }

    override suspend fun sendEmail(
        to: List<String>,
        subject: String,
        body: String,
        config: EmailConfig?,
    ): Result<Unit> {
        val emailConfig = config
            ?: emailConfigDataStore.getEmailConfig()
            ?: return Result.failure(Exception("Email configuration not found."))
        return emailService.sendEmail(to, subject, body, emailConfig)
    }
}
