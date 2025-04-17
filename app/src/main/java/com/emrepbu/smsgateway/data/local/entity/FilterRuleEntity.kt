package com.emrepbu.smsgateway.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.emrepbu.smsgateway.data.local.converter.StringListConverter
import com.emrepbu.smsgateway.domain.model.FilterRule

@Entity(tableName = "filter_rules")
@TypeConverters(StringListConverter::class)
data class FilterRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val senderContains: String?,
    val messageContains: String?,
    val excludeSenderContains: String?,
    val excludeMessageContains: String?,
    val isEnabled: Boolean,
    val emailAddresses: List<String>,
    var createdAt: Long,
) {
    fun toDomainModel(): FilterRule {
        return FilterRule(
            id = id,
            name = name,
            senderContains = senderContains,
            messageContains = messageContains,
            excludeSenderContains = excludeSenderContains,
            excludeMessageContains = excludeMessageContains,
            isEnabled = isEnabled,
            emailAddresses = emailAddresses,
            createdAt = createdAt,
        )
    }

    companion object {
        fun fromDomainModel(domain: FilterRule): FilterRuleEntity {
            return FilterRuleEntity(
                id = domain.id,
                name = domain.name,
                senderContains = domain.senderContains,
                messageContains = domain.messageContains,
                excludeSenderContains = domain.excludeSenderContains,
                excludeMessageContains = domain.excludeMessageContains,
                isEnabled = domain.isEnabled,
                emailAddresses = domain.emailAddresses,
                createdAt = domain.createdAt,
            )
        }
    }
}
