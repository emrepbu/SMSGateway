package com.emrepbu.smsgateway.domain.usecase

import com.emrepbu.smsgateway.domain.model.ApiConfig
import com.emrepbu.smsgateway.domain.repository.ApiConfigRepository
import javax.inject.Inject

/**
 * [UpdateApiConfigUseCase] is responsible for updating the API configuration settings.
 * 
 * It uses the [ApiConfigRepository] to persist the updated configuration to storage.
 * 
 * @property apiConfigRepository Repository for configuration settings.
 */
class UpdateApiConfigUseCase @Inject constructor(
    private val apiConfigRepository: ApiConfigRepository
) {
    /**
     * Updates the API configuration with the provided values.
     * 
     * @param apiConfig The new API configuration settings to save.
     */
    suspend operator fun invoke(apiConfig: ApiConfig) {
        apiConfigRepository.updateApiConfig(apiConfig)
    }
}
