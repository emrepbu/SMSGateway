package com.emrepbu.smsgateway.ui.viewmodel

import androidx.compose.material3.ripple
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.smsgateway.domain.model.FilterRule
import com.emrepbu.smsgateway.domain.repository.FilterRuleRepository
import com.emrepbu.smsgateway.ui.state.FilterRuleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterRuleViewModel @Inject constructor(
    private val filterRuleRepository: FilterRuleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FilterRuleState())
    val state = _state.asStateFlow()

    init {
        loadFilterRules()
    }

    private fun loadFilterRules() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            filterRuleRepository.getAllFilterRules()
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed load filter rule."
                    )
                }
                .collectLatest { rules ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        filterRules = rules,
                        error = null,
                    )
                }
        }
    }

    fun getFilterRule(id: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val rule = filterRuleRepository.getFilterRuleById(id)
                _state.value = _state.value.copy(
                    isLoading = false,
                    selectedRule = rule,
                    error = null,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed load filter rule."
                )
            }
        }
    }

    fun saveFilterRule(rule: FilterRule) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                if (rule.id == 0L) {
                    filterRuleRepository.insertFilterRule(rule)
                } else {
                    filterRuleRepository.updateFilterRule(rule)
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = null,
                )

                loadFilterRules()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed save filter rule."
                )
            }
        }
    }

    fun deleteFilterRule(id: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                filterRuleRepository.deleteFilterRule(id)

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = null,
                    selectedRule = null
                )

                loadFilterRules()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed delete filter rule."
                )
            }
        }
    }

    fun toggleFilterRuleEnabled(rule: FilterRule) {
        viewModelScope.launch {
            try {
                val updatedRule = rule.copy(isEnabled = !rule.isEnabled)
                filterRuleRepository.updateFilterRule(updatedRule)

                loadFilterRules()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed update filter rule."
                )
            }
        }
    }
}
