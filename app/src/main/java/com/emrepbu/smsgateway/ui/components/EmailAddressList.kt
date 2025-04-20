package com.emrepbu.smsgateway.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmailAddressList(
    emailAddresses: List<String>,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (emailAddresses.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No email addresses added",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    } else {
        Column(modifier = modifier.fillMaxWidth()) {
            emailAddresses.forEach { email ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { onRemove(email) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Email"
                        )
                    }
                }

                HorizontalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailAddressListPreview() {
    EmailAddressList(
        emailAddresses = listOf("test@example.com", "test2@example.com"),
        onRemove = {}
    )
}
