package com.emrepbu.smsgateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.smsgateway.domain.model.EmailConfig
import com.emrepbu.smsgateway.domain.repository.EmailRepository
import com.emrepbu.smsgateway.ui.state.EmailConfigState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailConfigViewModel @Inject constructor(
    private val emailRepository: EmailRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmailConfigState())
    val state = _state.asStateFlow()

    init {
        loadEmailConfig()
    }

    fun loadEmailConfig() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val config = emailRepository.getEmailConfig()
                _state.value = _state.value.copy(
                    emailConfig = config,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed load email config"
                )
            }
        }
    }

    fun saveEmailConfig(config: EmailConfig) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)

            try {
                emailRepository.saveEmailConfig(config)
                _state.value = _state.value.copy(
                    emailConfig = config,
                    isSaving = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed saving email config"
                )
            }
        }
    }

    fun sendTestEmail(to: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isTestSending = true,
                testSendResult = null
            )

            try {
                val config =
                    _state.value.emailConfig ?: throw Exception("Not found config")

                val result = emailRepository.sendEmail(
                    to = listOf(to),
                    subject = "SMS Gateway Test E-postası",
                    body = "Bu bir test e-postasıdır. E-posta gönderimi başarılı.",
                    config = config
                )

                if (result.isSuccess) {
                    _state.value = _state.value.copy(
                        isTestSending = false,
                        testSendResult = "Test e-postası başarıyla gönderildi",
                        error = null
                    )
                } else {
                    throw result.exceptionOrNull() ?: Exception("Unknown")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isTestSending = false,
                    testSendResult = "Test e-postası gönderilemedi: ${e.message}",
                    error = null
                )
            }
        }
    }
}
