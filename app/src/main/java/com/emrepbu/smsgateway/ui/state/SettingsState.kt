package com.emrepbu.smsgateway.ui.state

/**
 * Represents the state of the settings screen.
 *
 * This data class holds the various states related to the settings functionality,
 * including whether a service is enabled, if data is currently loading, and any
 * errors that may have occurred.
 *
 * @property isServiceEnabled Indicates whether the primary service associated with these settings is enabled.
 *                           Defaults to `false`.
 * @property isLoading Indicates whether a loading operation is in progress, such as fetching or updating settings.
 *                    Defaults to `false`.
 * @property error A nullable string representing an error message that occurred during a settings operation.
 *                 If `null`, no error is present. Defaults to `null`.
 */
data class SettingsState(
    val isServiceEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
