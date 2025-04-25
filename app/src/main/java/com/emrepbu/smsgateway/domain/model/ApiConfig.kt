package com.emrepbu.smsgateway.domain.model

/**
 * Represents API configuration settings for SMS forwarding.
 * 
 * @property enabled Indicates whether the API integration is enabled.
 * @property apiUrl The URL of the API to which SMS messages should be sent.
 * @property authToken Optional authentication token for API authorization.
 * @property customSenderName Optional custom sender name to use in API requests.
 */
data class ApiConfig(
    val enabled: Boolean = false,
    val apiUrl: String = "",
    val authToken: String = "",
    val customSenderName: String = ""
)
