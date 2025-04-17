package com.emrepbu.smsgateway.domain.repository

import com.emrepbu.smsgateway.domain.model.FilterRule
import kotlinx.coroutines.flow.Flow

interface FilterRuleRepository {
    fun getAllFilterRules(): Flow<List<FilterRule>>
    fun getEnabledFilterRules(): Flow<List<FilterRule>>
    suspend fun getFilterRuleById(id: Long): FilterRule?
    suspend fun insertFilterRule(rule: FilterRule): Long
    suspend fun updateFilterRule(rule: FilterRule)
    suspend fun deleteFilterRule(id: Long)
}
