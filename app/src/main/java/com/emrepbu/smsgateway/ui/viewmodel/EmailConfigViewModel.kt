package com.emrepbu.smsgateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.smsgateway.domain.model.EmailConfig
import com.emrepbu.smsgateway.domain.repository.EmailRepository
import com.emrepbu.smsgateway.ui.state.EmailConfigState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [EmailConfigViewModel] is a ViewModel responsible for managing the email configuration
 * and handling operations related to loading, saving, and testing the email setup.
 *
 * It interacts with [EmailRepository] to perform data operations and exposes the state
 * of the email configuration through a [StateFlow].
 *
 * @property emailRepository The repository used for accessing and manipulating email configuration data.
 */
@HiltViewModel
class EmailConfigViewModel @Inject constructor(
    private val emailRepository: EmailRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmailConfigState())
    val state = _state.asStateFlow()

    init {
        loadEmailConfig()
    }

    private fun loadEmailConfig() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val config = emailRepository.getEmailConfig()
                _state.update {
                    it.copy(
                        emailConfig = config,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load email configuration"
                    )
                }
            }
        }
    }

    fun saveEmailConfig(config: EmailConfig) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            try {
                emailRepository.saveEmailConfig(config)
                _state.update {
                    it.copy(
                        emailConfig = config,
                        isSaving = false,
                        error = null,
                        isSaved = true
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Failed to save email configuration"
                    )
                }
            }
        }
    }

    fun testEmailConfig(to: String) {
        sendTestEmail(to)
    }

    private fun sendTestEmail(to: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isTestSending = true,
                    testSendResult = null
                )
            }

            try {
                val config = _state.value.emailConfig
                    ?: throw Exception("Email configuration not found")

                val result = emailRepository.sendEmail(
                    to = listOf(to),
                    subject = "SMS Gateway Test Email",
                    body = "This is a test email from SMS Gateway app. If you receive this, email sending is working properly.",
                    config = config
                )

                if (result.isSuccess) {
                    _state.update {
                        it.copy(
                            isTestSending = false,
                            testSendResult = "Test email sent successfully",
                            error = null
                        )
                    }
                } else {
                    throw result.exceptionOrNull() ?: Exception("Unknown error")
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isTestSending = false,
                        testSendResult = "Failed to send test email: ${e.message}",
                        error = null
                    )
                }
            }
        }
    }

    fun clearTestResult() {
        _state.update { it.copy(testSendResult = null) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetSaveState() {
        _state.update { it.copy(isSaved = false) }
    }
}
