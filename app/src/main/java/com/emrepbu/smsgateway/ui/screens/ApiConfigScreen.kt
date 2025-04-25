package com.emrepbu.smsgateway.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emrepbu.smsgateway.ui.viewmodel.ApiConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiConfigScreen(
    onBack: () -> Unit,
    viewModel: ApiConfigViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            snackbarHostState.showSnackbar("API configuration saved successfully")
            viewModel.resetSaveFlag()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Configuration") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable API Integration",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = state.isEnabled,
                        onCheckedChange = { viewModel.setEnabled(it) }
                    )
                }

                OutlinedTextField(
                    value = state.apiUrl,
                    onValueChange = { viewModel.updateApiUrl(it) },
                    label = { Text("API URL") },
                    placeholder = { Text("https://api.example.com/sms") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.isEnabled
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.authToken,
                    onValueChange = { viewModel.updateAuthToken(it) },
                    label = { Text("Authentication Token (Optional)") },
                    placeholder = { Text("Your API token") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.isEnabled
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.customSenderName,
                    onValueChange = { viewModel.updateCustomSenderName(it) },
                    label = { Text("Custom Sender Name (Optional)") },
                    placeholder = { Text("Your App Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.isEnabled
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.saveConfig() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && state.isEnabled
                ) {
                    Text("Save Configuration")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Configure the API endpoint to which SMS messages will be forwarded. " +
                           "Ensure the URL is correct and accessible from your device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
