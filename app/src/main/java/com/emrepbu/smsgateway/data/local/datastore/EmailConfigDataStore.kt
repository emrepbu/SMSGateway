package com.emrepbu.smsgateway.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emrepbu.smsgateway.domain.model.EmailConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

val Context.emailConfigDataStore: DataStore<Preferences> by preferencesDataStore(name = "email_config")

@Singleton
class EmailConfigDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun saveEmailConfig(config: EmailConfig) {
        context.emailConfigDataStore.edit { preferences ->
            preferences[SMTP_SERVER] = config.smtpServer
            preferences[SMTP_PORT] = config.smtpPort
            preferences[USERNAME] = config.username
            preferences[PASSWORD] = config.password
            preferences[FROM_ADDRESS] = config.fromAddress
            preferences[USE_SSL] = config.useSsl
        }
    }

    suspend fun getEmailConfig(): EmailConfig? {
        val preferences = context.emailConfigDataStore.data.first()

        val smtpServer = preferences[SMTP_SERVER] ?: return null
        val smtpPort = preferences[SMTP_PORT] ?: return null
        val username = preferences[USERNAME] ?: return null
        val password = preferences[PASSWORD] ?: return null
        val fromAddress = preferences[FROM_ADDRESS] ?: return null
        val useSsl = preferences[USE_SSL] ?: true

        return EmailConfig(
            smtpServer = smtpServer,
            smtpPort = smtpPort,
            username = username,
            password = password,
            fromAddress = fromAddress,
            useSsl = useSsl,
        )
    }

    companion object {
        private val SMTP_SERVER = stringPreferencesKey("smtp_server")
        private val SMTP_PORT = intPreferencesKey("smtp_port")
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")
        private val FROM_ADDRESS = stringPreferencesKey("from_address")
        private val USE_SSL = booleanPreferencesKey("use_ssl")
    }
}
