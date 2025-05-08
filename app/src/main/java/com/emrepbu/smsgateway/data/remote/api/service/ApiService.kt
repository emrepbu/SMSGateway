package com.emrepbu.smsgateway.data.remote.api.service

import android.util.Log
import com.emrepbu.smsgateway.data.remote.api.model.ApiResponse
import com.emrepbu.smsgateway.data.remote.api.model.ApiResult
import com.emrepbu.smsgateway.data.remote.api.model.SmsRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ApiService"

@Singleton
class ApiService @Inject constructor() {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000 // 15 saniye
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }

    suspend fun sendSms(apiUrl: String, phoneNumber: String, message: String, senderName: String? = null, authToken: String? = null): ApiResult<ApiResponse> {
        return try {
            withContext(Dispatchers.IO) {
                val response = client.post(apiUrl) {
                    setBody(SmsRequest(phoneNumber, message, senderName))
                    if (!authToken.isNullOrBlank()) {
                        header("Authorization", "Bearer $authToken")
                    }
                }
                
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val responseData: ApiResponse = response.body()
                        ApiResult.Success(responseData)
                    }
                    else -> {
                        ApiResult.Error("API isteği başarısız: ${response.status.description}", response.status.value)
                    }
                }
            }
        } catch (e: ClientRequestException) {
            Log.e(TAG, "HTTP Hatası: ${e.response.status.value}")
            ApiResult.Error("HTTP Hatası: ${e.response.status.description}", e.response.status.value)
        } catch (e: ServerResponseException) {
            Log.e(TAG, "Sunucu Hatası: ${e.response.status.value}")
            ApiResult.Error("Sunucu Hatası: ${e.response.status.description}", e.response.status.value)
        } catch (e: HttpRequestTimeoutException) {
            Log.e(TAG, "Bağlantı zaman aşımına uğradı", e)
            ApiResult.Error("Bağlantı zaman aşımına uğradı. İnternet bağlantınızı kontrol edin.")
        } catch (e: Exception) {
            Log.e(TAG, "API isteği sırasında hata oluştu", e)
            ApiResult.Error("İstek gönderilirken bir hata oluştu: ${e.localizedMessage}")
        }
    }

    fun close() {
        client.close()
    }
}
