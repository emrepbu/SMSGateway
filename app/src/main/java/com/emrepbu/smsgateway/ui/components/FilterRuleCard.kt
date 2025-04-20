package com.emrepbu.smsgateway.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emrepbu.smsgateway.domain.model.FilterRule

@Composable
fun FilterRuleCard(
    rule: FilterRule,
    onClick: () -> Unit,
    onToggleEnabled: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = { onToggleEnabled() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!rule.senderContains.isNullOrBlank()) {
                FilterCriteriaRow(
                    label = "Sender contains:",
                    value = rule.senderContains
                )
            }

            if (!rule.messageContains.isNullOrBlank()) {
                FilterCriteriaRow(
                    label = "Message contains:",
                    value = rule.messageContains
                )
            }

            if (!rule.excludeSenderContains.isNullOrBlank()) {
                FilterCriteriaRow(
                    label = "Exclude if sender contains:",
                    value = rule.excludeSenderContains
                )
            }

            if (!rule.excludeMessageContains.isNullOrBlank()) {
                FilterCriteriaRow(
                    label = "Exclude if message contains:",
                    value = rule.excludeMessageContains
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${rule.emailAddresses.size} recipient(s)",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun PreviewFilterRuleCard() {
    val rule by lazy {
        FilterRule(
            id = 1,
            name = "Preview Rule",
            isEnabled = true,
            senderContains = "Sender",
            messageContains = "Message",
            excludeSenderContains = "Exclude Sender",
            excludeMessageContains = "Exclude Message",
            emailAddresses = listOf("test@test.com", "test2@test.com")
        )
    }
    FilterRuleCard(
        rule = rule,
        onClick = {
            // Handle click
        },
        onToggleEnabled = {
            // Handle toggle
        }
    )
}

