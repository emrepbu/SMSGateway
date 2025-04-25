package com.emrepbu.smsgateway.domain.usecase

import com.emrepbu.smsgateway.domain.model.ApiConfig
import com.emrepbu.smsgateway.domain.repository.ApiConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * [GetApiConfigUseCase] is responsible for retrieving the API configuration settings.
 * 
 * It fetches the API configuration from the [ApiConfigRepository], which may retrieve
 * the data from a local data store such as SharedPreferences or a database.
 * 
 * @property apiConfigRepository Repository for configuration settings.
 */
class GetApiConfigUseCase @Inject constructor(
    private val apiConfigRepository: ApiConfigRepository
) {
    /**
     * Gets the current API configuration as a Flow to observe changes.
     * 
     * @return A Flow emitting the current API configuration.
     */
    operator fun invoke(): Flow<ApiConfig> {
        return apiConfigRepository.getApiConfig()
    }
}
