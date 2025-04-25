package com.emrepbu.smsgateway.domain.repository

import com.emrepbu.smsgateway.domain.model.ApiConfig
import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing and modifying configuration settings.
 */
interface ApiConfigRepository {
    /**
     * Gets the API configuration settings.
     * 
     * @return A Flow emitting the current API configuration.
     */
    fun getApiConfig(): Flow<ApiConfig>
    
    /**
     * Updates the API configuration settings.
     * 
     * @param apiConfig The new API configuration settings.
     */
    suspend fun updateApiConfig(apiConfig: ApiConfig)
}
