package com.emrepbu.smsgateway.data.repository

import com.emrepbu.smsgateway.data.remote.api.model.ApiResponse
import com.emrepbu.smsgateway.data.remote.api.model.ApiResult
import com.emrepbu.smsgateway.data.remote.api.service.ApiService
import com.emrepbu.smsgateway.domain.repository.ApiConfigRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiConfigRepository: ApiConfigRepository
) {
    suspend fun sendSms(phoneNumber: String, message: String, senderName: String? = null): ApiResult<ApiResponse> {
        val apiConfig = apiConfigRepository.getApiConfig().first()
        
        if (!apiConfig.enabled || apiConfig.apiUrl.isBlank()) {
            return ApiResult.Error("API integration is not properly configured")
        }
        
        return apiService.sendSms(
            apiUrl = apiConfig.apiUrl,
            phoneNumber = phoneNumber,
            message = message,
            senderName = senderName ?: apiConfig.customSenderName.takeIf { it.isNotBlank() },
            authToken = apiConfig.authToken.takeIf { it.isNotBlank() }
        )
    }
}
