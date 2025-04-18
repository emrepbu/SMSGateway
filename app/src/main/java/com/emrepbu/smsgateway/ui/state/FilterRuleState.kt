package com.emrepbu.smsgateway.ui.state

import com.emrepbu.smsgateway.domain.model.FilterRule

data class FilterRuleState(
    val filterRules: List<FilterRule> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedRule: FilterRule? = null
)
