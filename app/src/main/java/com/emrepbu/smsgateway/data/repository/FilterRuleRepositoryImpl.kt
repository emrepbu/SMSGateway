package com.emrepbu.smsgateway.data.repository

import com.emrepbu.smsgateway.data.local.dao.FilterRuleDao
import com.emrepbu.smsgateway.data.local.entity.FilterRuleEntity
import com.emrepbu.smsgateway.domain.model.FilterRule
import com.emrepbu.smsgateway.domain.repository.FilterRuleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterRuleRepositoryImpl @Inject constructor(
    private val filterRuleDao: FilterRuleDao,
) : FilterRuleRepository {

    override fun getAllFilterRules(): Flow<List<FilterRule>> {
        return filterRuleDao.getAllFilterRules().map {
            it.map(FilterRuleEntity::toDomainModel)
        }
    }

    override fun getEnabledFilterRules(): Flow<List<FilterRule>> {
        return filterRuleDao.getEnabledFilterRules().map {
            it.map(FilterRuleEntity::toDomainModel)
        }
    }

    override suspend fun getFilterRuleById(id: Long): FilterRule? {
        return filterRuleDao.getFilterRuleById(id)?.toDomainModel()
    }

    override suspend fun insertFilterRule(rule: FilterRule): Long {
        return filterRuleDao.insertFilterRule(FilterRuleEntity.fromDomainModel(rule))
    }

    override suspend fun updateFilterRule(rule: FilterRule) {
        filterRuleDao.updateFilterRule(FilterRuleEntity.fromDomainModel(rule))
    }

    override suspend fun deleteFilterRule(id: Long) {
        filterRuleDao.deleteFilterRule(id)
    }
}
