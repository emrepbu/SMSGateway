package com.emrepbu.smsgateway.ui.viewmodel

import androidx.hilt.work.HiltWorker
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.smsgateway.domain.repository.SmsRepository
import com.emrepbu.smsgateway.ui.state.SmsListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmsViewModel @Inject constructor(
    private val smsRepository: SmsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SmsListState())
    val state = _state.asStateFlow()

    init {
        loadSmsMessage()
    }

    private fun loadSmsMessage() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            smsRepository.getAllSmsMessage()
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed load sms messages."
                    )
                }
                .collectLatest { messages ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        smsMessage = messages,
                        error = null,
                    )
                }
        }
    }

    fun refreshSmsMessages() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                smsRepository.refreshSmsFromSystem()
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed refresh sms message."
                )
            }
        }
    }
}