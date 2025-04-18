package com.emrepbu.smsgateway.ui.state

import com.emrepbu.smsgateway.domain.model.EmailConfig

data class EmailConfigState(
    val emailConfig: EmailConfig? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isTestSending: Boolean = false,
    val testSendResult: String? = null
)
