package com.emrepbu.smsgateway.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.emrepbu.smsgateway.data.local.converter.StringListConverter
import com.emrepbu.smsgateway.domain.model.SmsMessage

/**
 * Entity class representing an SMS message in the local database.
 * This is part of the data layer and follows the repository pattern.
 */
@Entity(tableName = "sms_messages")
@TypeConverters(StringListConverter::class)
data class SmsMessageEntity(
    @PrimaryKey
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val type: Int,
    val isForwarded: Boolean = false,
    val forwardedTo: List<String> = emptyList(),
    var forwardedAt: Long? = null,
) {
    fun toDomainModel(): SmsMessage {
        return SmsMessage(
            id = id,
            sender = sender,
            message = message,
            timestamp = timestamp,
            isRead = isRead,
            type = type,
            isForwarded = isForwarded,
            forwardedTo = forwardedTo,
            forwardedAt = forwardedAt,
        )
    }

    companion object {
        fun fromDomainModel(domain: SmsMessage): SmsMessageEntity {
            return SmsMessageEntity(
                id = domain.id,
                sender = domain.sender,
                message = domain.message,
                timestamp = domain.timestamp,
                isRead = domain.isRead,
                type = domain.type,
                isForwarded = domain.isForwarded,
                forwardedTo = domain.forwardedTo,
                forwardedAt = domain.forwardedAt,
            )
        }
    }
}
