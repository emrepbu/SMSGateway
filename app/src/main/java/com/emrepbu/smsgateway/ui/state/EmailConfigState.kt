package com.emrepbu.smsgateway.ui.state

import com.emrepbu.smsgateway.domain.model.EmailConfig

/**
 * Represents the state of the Email Configuration screen.
 *
 * This data class holds all the necessary information to represent the current state
 * of the email configuration, including the configuration itself, loading and saving states,
 * error messages, and the result of a test email send.
 *
 * @property emailConfig The current [EmailConfig] object, or null if no configuration is loaded.
 * @property isLoading True if the email configuration is currently being loaded, false otherwise.
 * @property isSaving True if the email configuration is currently being saved, false otherwise.
 * @property error An optional error message to display if an error occurred. Null if no error.
 * @property isTestSending True if a test email is currently being sent, false otherwise.
 * @property testSendResult The result message of the last test email send, or null if no test has been run or the result is pending.
 * @property isSaved True if the email configuration has been successfully saved, false otherwise.
 */
data class EmailConfigState(
    val emailConfig: EmailConfig? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isTestSending: Boolean = false,
    val testSendResult: String? = null,
    val isSaved: Boolean = false
)
