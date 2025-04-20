package com.emrepbu.smsgateway.ui.state

import com.emrepbu.smsgateway.domain.model.SmsMessage

/**
 * Enum class representing the filtering options for SMS messages.
 *
 * This enum provides options to specify whether to include all SMS messages or
 * only those that match certain filtering criteria.
 */
enum class SmsFilter {
    ALL, FILTERED_ONLY
}

/**
 * Represents the state of the SMS message list.
 *
 * This data class holds all the information related to the current state of the
 * list of SMS messages being displayed. It includes the list of messages,
 * whether the list is currently loading, any errors encountered, and the
 * currently applied filter.
 *
 * @property smsMessages The list of SMS messages to be displayed. Defaults to an empty list.
 * @property isLoading True if the list is currently being loaded; false otherwise. Defaults to false.
 * @property error An error message, if any error occurred while loading or processing SMS messages.
 *                  Null if there is no error. Defaults to null.
 * @property selectedFilter The filter currently applied to the SMS list. Defaults to [SmsFilter.ALL].
 */
data class SmsListState(
    val smsMessages: List<SmsMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: SmsFilter = SmsFilter.ALL
)
