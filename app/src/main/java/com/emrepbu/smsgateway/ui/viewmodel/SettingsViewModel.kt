package com.emrepbu.smsgateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.smsgateway.ui.state.SettingsState
import com.emrepbu.smsgateway.work.SmsWorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the state and logic of the settings screen.
 *
 * This ViewModel interacts with the [SmsWorkManager] to schedule and cancel SMS checking work.
 * It exposes a state flow ([state]) that reflects the current settings and service status.
 *
 * @property smsWorkManager An instance of [SmsWorkManager] for managing background SMS checking tasks.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val smsWorkManager: SmsWorkManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()
    
    init {
        checkServiceStatus()
    }
    
    private fun checkServiceStatus() {
        _state.update { it.copy(isServiceEnabled = true) }
    }
    
    fun setServiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                if (enabled) {
                    smsWorkManager.scheduleSmsCheckWork()
                } else {
                    smsWorkManager.cancelSmsCheckWork()
                }
                
                _state.update { 
                    it.copy(
                        isServiceEnabled = enabled,
                        isLoading = false,
                        error = null
                    )
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
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
