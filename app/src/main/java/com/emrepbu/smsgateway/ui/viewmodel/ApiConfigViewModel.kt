package com.emrepbu.smsgateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.smsgateway.domain.model.ApiConfig
import com.emrepbu.smsgateway.domain.usecase.GetApiConfigUseCase
import com.emrepbu.smsgateway.domain.usecase.UpdateApiConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiConfigViewModel @Inject constructor(
    private val getApiConfigUseCase: GetApiConfigUseCase,
    private val updateApiConfigUseCase: UpdateApiConfigUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ApiConfigState())
    val state: StateFlow<ApiConfigState> = _state.asStateFlow()

    init {
        loadApiConfig()
    }

    private fun loadApiConfig() {
        viewModelScope.launch {
            getApiConfigUseCase().collectLatest { apiConfig ->
                _state.update { it.copy(
                    isEnabled = apiConfig.enabled,
                    apiUrl = apiConfig.apiUrl,
                    authToken = apiConfig.authToken,
                    customSenderName = apiConfig.customSenderName,
                    isLoading = false
                ) }
            }
        }
    }

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateApiConfigUseCase(
                ApiConfig(
                    enabled = enabled,
                    apiUrl = state.value.apiUrl,
                    authToken = state.value.authToken,
                    customSenderName = state.value.customSenderName
                )
            )
        }
    }

    fun updateApiUrl(url: String) {
        _state.update { it.copy(apiUrl = url) }
    }

    fun updateAuthToken(token: String) {
        _state.update { it.copy(authToken = token) }
    }

    fun updateCustomSenderName(name: String) {
        _state.update { it.copy(customSenderName = name) }
    }

    fun saveConfig() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateApiConfigUseCase(
                ApiConfig(
                    enabled = state.value.isEnabled,
                    apiUrl = state.value.apiUrl,
                    authToken = state.value.authToken,
                    customSenderName = state.value.customSenderName
                )
            )
            _state.update { it.copy(isSaved = true, isLoading = false) }
        }
    }

    fun resetSaveFlag() {
        _state.update { it.copy(isSaved = false) }
    }
}

data class ApiConfigState(
    val isEnabled: Boolean = false,
    val apiUrl: String = "",
    val authToken: String = "",
    val customSenderName: String = "",
    val isLoading: Boolean = true,
    val isSaved: Boolean = false
)
