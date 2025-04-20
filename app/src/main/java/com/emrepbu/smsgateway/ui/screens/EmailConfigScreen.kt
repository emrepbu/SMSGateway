package com.emrepbu.smsgateway.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emrepbu.smsgateway.domain.model.EmailConfig
import com.emrepbu.smsgateway.ui.viewmodel.EmailConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailConfigScreen(
    viewModel: EmailConfigViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val emailConfig = state.emailConfig ?: EmailConfig(
        smtpServer = "",
        smtpPort = 587,
        username = "",
        password = "",
        fromAddress = "",
        useSsl = true
    )

    var smtpServer by remember(emailConfig.smtpServer) { mutableStateOf(emailConfig.smtpServer) }
    var smtpPort by remember(emailConfig.smtpPort) { mutableStateOf(emailConfig.smtpPort.toString()) }
    var username by remember(emailConfig.username) { mutableStateOf(emailConfig.username) }
    var password by remember(emailConfig.password) { mutableStateOf(emailConfig.password) }
    var fromAddress by remember(emailConfig.fromAddress) { mutableStateOf(emailConfig.fromAddress) }
    var useSsl by remember(emailConfig.useSsl) { mutableStateOf(emailConfig.useSsl) }

    var passwordVisible by remember { mutableStateOf(false) }
    var testEmailAddress by remember { mutableStateOf("") }
    var showTestDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            // TODO
            // Show a snackbar or some indication that save was successful
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Email Configuration") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                value = smtpServer,
                onValueChange = { smtpServer = it },
                label = { Text("SMTP Server") },
                modifier = Modifier.fillMaxWidth(),
                isError = smtpServer.isBlank(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = smtpPort,
                onValueChange = { smtpPort = it.filter { char -> char.isDigit() } },
                label = { Text("SMTP Port") },
                modifier = Modifier.fillMaxWidth(),
                isError = smtpPort.isBlank() || smtpPort.toIntOrNull() == null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = username.isBlank(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                isError = password.isBlank(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Hide" else "Show")
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = fromAddress,
                onValueChange = { fromAddress = it },
                label = { Text("From Email Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = fromAddress.isBlank() || !fromAddress.contains("@"),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = useSsl,
                    onCheckedChange = { useSsl = it }
                )
                Text("Use SSL")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val config = EmailConfig(
                            smtpServer = smtpServer,
                            smtpPort = smtpPort.toIntOrNull() ?: 587,
                            username = username,
                            password = password,
                            fromAddress = fromAddress,
                            useSsl = useSsl
                        )
                        viewModel.saveEmailConfig(config)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = smtpServer.isNotBlank() &&
                            smtpPort.toIntOrNull() != null &&
                            username.isNotBlank() &&
                            password.isNotBlank() &&
                            fromAddress.isNotBlank() &&
                            fromAddress.contains("@")
                ) {
                    Text("Save")
                }

                OutlinedButton(
                    onClick = { showTestDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = state.emailConfig != null
                ) {
                    Text("Test")
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        if (showTestDialog) {
            AlertDialog(
                onDismissRequest = { showTestDialog = false },
                title = { Text("Test Email Configuration") },
                text = {
                    Column {
                        Text("Enter an email address to send a test email:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = testEmailAddress,
                            onValueChange = { testEmailAddress = it },
                            label = { Text("Test Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = testEmailAddress.isBlank() || !testEmailAddress.contains("@"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (testEmailAddress.isNotBlank() && testEmailAddress.contains("@")) {
                                viewModel.testEmailConfig(testEmailAddress)
                                showTestDialog = false
                            }
                        },
                        enabled = testEmailAddress.isNotBlank() && testEmailAddress.contains("@")
                    ) {
                        Text("Send Test")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTestDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
