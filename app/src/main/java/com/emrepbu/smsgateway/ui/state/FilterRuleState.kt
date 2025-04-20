package com.emrepbu.smsgateway.ui.state

import com.emrepbu.smsgateway.domain.model.FilterRule

/**
 * Represents the state of the filter rule management screen.
 *
 * This data class holds the current state of the filter rules, including the list of available rules,
 * loading status, any error messages, and the currently selected rule (if any).
 *
 * @property filterRules The list of available filter rules. Defaults to an empty list.
 * @property isLoading Indicates whether the filter rules are currently being loaded. Defaults to false.
 * @property error An optional error message if an error occurred while loading or managing filter rules. Defaults to null.
 * @property selectedRule The currently selected filter rule, or null if no rule is selected. Defaults to null.
 */
data class FilterRuleState(
    val filterRules: List<FilterRule> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedRule: FilterRule? = null
)
