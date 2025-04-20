package com.emrepbu.smsgateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.smsgateway.domain.repository.FilterRuleRepository
import com.emrepbu.smsgateway.domain.repository.SmsRepository
import com.emrepbu.smsgateway.ui.state.SmsFilter
import com.emrepbu.smsgateway.ui.state.SmsListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [SmsViewModel] is a [ViewModel] responsible for managing the state and logic related to SMS messages.
 * It interacts with [SmsRepository] to fetch and refresh SMS messages and with [FilterRuleRepository] to apply filtering rules.
 *
 * This ViewModel handles:
 * - Loading SMS messages (both all and filtered).
 * - Refreshing SMS messages from the system.
 * - Applying filters to the SMS message list.
 * - Managing loading and error states.
 *
 * @property smsRepository The repository for interacting with SMS messages.
 * @property filterRuleRepository The repository for interacting with filter rules.
 */
@HiltViewModel
class SmsViewModel @Inject constructor(
    private val smsRepository: SmsRepository,
    private val filterRuleRepository: FilterRuleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SmsListState())
    val state = _state.asStateFlow()

    init {
        loadSmsMessages()
    }

    fun loadSmsMessages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (_state.value.selectedFilter) {
                SmsFilter.ALL -> loadAllMessages()
                SmsFilter.FILTERED_ONLY -> loadFilteredMessages()
            }
        }
    }
    
    private suspend fun loadAllMessages() {
        smsRepository.getAllSmsMessage()
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load SMS messages."
                    )
                }
            }
            .collectLatest { messages ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        smsMessages = messages,
                        error = null
                    )
                }
            }
    }
    
    private suspend fun loadFilteredMessages() {
        // Get all enabled filter rules
        filterRuleRepository.getEnabledFilterRules().collect { enabledRules ->
            if (enabledRules.isEmpty()) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        smsMessages = emptyList(),
                        error = null
                    )
                }
                return@collect
            }
            
            smsRepository.getAllSmsMessage().collect { allMessages ->
                val filteredMessages = allMessages.filter { sms ->
                    enabledRules.any { rule ->
                        // Sender matching
                        val senderMatch = rule.senderContains.isNullOrEmpty() ||
                                sms.sender.contains(rule.senderContains!!, ignoreCase = true)
                        
                        // Sender exclusion
                        val senderExcludeMatch = rule.excludeSenderContains.isNullOrEmpty() ||
                                !sms.sender.contains(rule.excludeSenderContains!!, ignoreCase = true)
                        
                        // Message matching
                        val messageMatch = rule.messageContains.isNullOrEmpty() ||
                                sms.message.contains(rule.messageContains!!, ignoreCase = true)
                        
                        // Message exclusion
                        val messageExcludeMatch = rule.excludeMessageContains.isNullOrEmpty() ||
                                !sms.message.contains(rule.excludeMessageContains!!, ignoreCase = true)
                        
                        senderMatch && senderExcludeMatch && messageMatch && messageExcludeMatch
                    }
                }
                
                _state.update {
                    it.copy(
                        isLoading = false,
                        smsMessages = filteredMessages,
                        error = null
                    )
                }
            }
        }
    }

    fun refreshSmsMessages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                smsRepository.refreshSmsFromSystem()
                loadSmsMessages()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to refresh SMS messages."
                    )
                }
            }
        }
    }

    fun setFilter(filter: SmsFilter) {
        _state.update { it.copy(selectedFilter = filter) }
        loadSmsMessages()
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}