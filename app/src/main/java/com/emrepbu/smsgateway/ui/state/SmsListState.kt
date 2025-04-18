package com.emrepbu.smsgateway.ui.state

import com.emrepbu.smsgateway.domain.model.SmsMessage

data class SmsListState(
    val smsMessage: List<SmsMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String? = null
)
