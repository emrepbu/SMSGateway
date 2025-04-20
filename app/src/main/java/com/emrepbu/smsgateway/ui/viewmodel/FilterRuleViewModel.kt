package com.emrepbu.smsgateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.smsgateway.domain.model.FilterRule
import com.emrepbu.smsgateway.domain.repository.FilterRuleRepository
import com.emrepbu.smsgateway.ui.state.FilterRuleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing and interacting with FilterRule data.
 *
 * This ViewModel provides methods for loading, retrieving, saving, deleting,
 * and toggling the enabled state of FilterRules. It exposes a [state] Flow
 * that UI components can observe to reflect changes in the FilterRule data.
 *
 * @property filterRuleRepository The repository responsible for data access
 *                                of FilterRule entities.
 */
@HiltViewModel
class FilterRuleViewModel @Inject constructor(
    private val filterRuleRepository: FilterRuleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FilterRuleState())
    val state = _state.asStateFlow()

    init {
        loadFilterRules()
    }

    fun loadFilterRules() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            filterRuleRepository.getAllFilterRules()
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load filter rules."
                        )
                    }
                }
                .collectLatest { rules ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            filterRules = rules,
                            error = null
                        )
                    }
                }
        }
    }

    fun getFilterRule(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val rule = filterRuleRepository.getFilterRuleById(id)
                _state.update {
                    it.copy(
                        isLoading = false,
                        selectedRule = rule,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load filter rule."
                    )
                }
            }
        }
    }

    fun saveFilterRule(rule: FilterRule) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                if (rule.id == 0L) {
                    filterRuleRepository.insertFilterRule(rule)
                } else {
                    filterRuleRepository.updateFilterRule(rule)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }

                loadFilterRules()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to save filter rule."
                    )
                }
            }
        }
    }

    fun deleteFilterRule(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                filterRuleRepository.deleteFilterRule(id)

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        selectedRule = null
                    )
                }

                loadFilterRules()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to delete filter rule."
                    )
                }
            }
        }
    }

    fun toggleFilterRuleEnabled(rule: FilterRule) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val updatedRule = rule.copy(isEnabled = !rule.isEnabled)
                filterRuleRepository.updateFilterRule(updatedRule)
                loadFilterRules()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to update filter rule."
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun loadFilterRule(id: String) {
        if (id == "new") {
            _state.update { it.copy(selectedRule = null) }
            return
        }

        try {
            val ruleId = id.toLong()
            getFilterRule(ruleId)
        } catch (e: NumberFormatException) {
            _state.update { it.copy(error = "Invalid rule ID format") }
        }
    }

    fun toggleRuleEnabled(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val rule = filterRuleRepository.getFilterRuleById(id)
                if (rule != null) {
                    val updatedRule = rule.copy(isEnabled = enabled)
                    filterRuleRepository.updateFilterRule(updatedRule)
                    _state.update { it.copy(isLoading = false, error = null) }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Rule not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}
