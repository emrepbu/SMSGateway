package com.emrepbu.smsgateway.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class SmsRequest(
    val phoneNumber: String,
    val message: String,
    val senderName: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: String? = null
)

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}
