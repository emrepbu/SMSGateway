package com.emrepbu.smsgateway.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emrepbu.smsgateway.domain.model.FilterRule
import com.emrepbu.smsgateway.ui.components.EmailAddressList
import com.emrepbu.smsgateway.ui.viewmodel.FilterRuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterRuleDetailScreen(
    ruleId: String,
    viewModel: FilterRuleViewModel = hiltViewModel(),
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isNew = ruleId == "new"

    LaunchedEffect(key1 = ruleId) {
        if (!isNew) {
            viewModel.loadFilterRule(ruleId)
        }
    }

    val currentRule = state.selectedRule ?: FilterRule(
        name = "",
        emailAddresses = emptyList()
    )

    var name by remember(currentRule.name) { mutableStateOf(currentRule.name) }
    var senderContains by remember(currentRule.senderContains) {
        mutableStateOf(
            currentRule.senderContains ?: ""
        )
    }
    var messageContains by remember(currentRule.messageContains) {
        mutableStateOf(
            currentRule.messageContains ?: ""
        )
    }
    var excludeSenderContains by remember(currentRule.excludeSenderContains) {
        mutableStateOf(
            currentRule.excludeSenderContains ?: ""
        )
    }
    var excludeMessageContains by remember(currentRule.excludeMessageContains) {
        mutableStateOf(
            currentRule.excludeMessageContains ?: ""
        )
    }
    var emailAddresses by remember(currentRule.emailAddresses) { mutableStateOf(currentRule.emailAddresses) }
    var isEnabled by remember(currentRule.isEnabled) { mutableStateOf(currentRule.isEnabled) }

    var emailAddressInput by remember { mutableStateOf("") }

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Create Filter Rule" else "Edit Filter Rule") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isNew) {
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Rule"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Rule Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = name.isBlank(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Matching Criteria",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = senderContains,
                onValueChange = { senderContains = it },
                label = { Text("Sender Contains") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = messageContains,
                onValueChange = { messageContains = it },
                label = { Text("Message Contains") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Exclusion Criteria",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = excludeSenderContains,
                onValueChange = { excludeSenderContains = it },
                label = { Text("Exclude If Sender Contains") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = excludeMessageContains,
                onValueChange = { excludeMessageContains = it },
                label = { Text("Exclude If Message Contains") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it }
                )
                Text("Rule Enabled")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Email Recipients",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = emailAddressInput,
                    onValueChange = { emailAddressInput = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (emailAddressInput.isNotBlank()) {
                            emailAddresses = emailAddresses + emailAddressInput
                            emailAddressInput = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            EmailAddressList(
                emailAddresses = emailAddresses,
                onRemove = { address ->
                    emailAddresses = emailAddresses.filter { it != address }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val updatedRule = FilterRule(
                        id = if (isNew) 0 else currentRule.id,
                        name = name,
                        senderContains = senderContains.takeIf { it.isNotBlank() },
                        messageContains = messageContains.takeIf { it.isNotBlank() },
                        excludeSenderContains = excludeSenderContains.takeIf { it.isNotBlank() },
                        excludeMessageContains = excludeMessageContains.takeIf { it.isNotBlank() },
                        isEnabled = isEnabled,
                        emailAddresses = emailAddresses,
                        createdAt = currentRule.createdAt
                    )
                    viewModel.saveFilterRule(updatedRule)
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && emailAddresses.isNotEmpty()
            ) {
                Text("Save Rule")
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Rule") },
                text = { Text("Are you sure you want to delete this rule?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteFilterRule(currentRule.id)
                            showDeleteConfirmation = false
                            onBack()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
