package com.emrepbu.smsgateway.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.emrepbu.smsgateway.domain.model.ApiConfig
import com.emrepbu.smsgateway.domain.repository.ApiConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiConfigRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ApiConfigRepository {

    private object PreferencesKeys {
        val API_ENABLED = booleanPreferencesKey("api_enabled")
        val API_URL = stringPreferencesKey("api_url")
        val API_AUTH_TOKEN = stringPreferencesKey("api_auth_token")
        val API_CUSTOM_SENDER_NAME = stringPreferencesKey("api_custom_sender_name")
    }

    override fun getApiConfig(): Flow<ApiConfig> {
        return dataStore.data.map { preferences ->
            ApiConfig(
                enabled = preferences[PreferencesKeys.API_ENABLED] ?: false,
                apiUrl = preferences[PreferencesKeys.API_URL] ?: "",
                authToken = preferences[PreferencesKeys.API_AUTH_TOKEN] ?: "",
                customSenderName = preferences[PreferencesKeys.API_CUSTOM_SENDER_NAME] ?: ""
            )
        }
    }

    override suspend fun updateApiConfig(apiConfig: ApiConfig) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.API_ENABLED] = apiConfig.enabled
            preferences[PreferencesKeys.API_URL] = apiConfig.apiUrl
            preferences[PreferencesKeys.API_AUTH_TOKEN] = apiConfig.authToken
            preferences[PreferencesKeys.API_CUSTOM_SENDER_NAME] = apiConfig.customSenderName
        }
    }
}
