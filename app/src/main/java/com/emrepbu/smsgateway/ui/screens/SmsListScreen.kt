package com.emrepbu.smsgateway.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emrepbu.smsgateway.ui.components.SmsCard
import com.emrepbu.smsgateway.ui.state.SmsFilter
import com.emrepbu.smsgateway.ui.viewmodel.SmsViewModel
import com.emrepbu.smsgateway.utils.AppEvents
import com.emrepbu.smsgateway.utils.PermissionUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsListScreen(
    viewModel: SmsViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    
    var hasPermissions by remember { mutableStateOf(PermissionUtils.hasSmsPermissions(context)) }
    
    LaunchedEffect(key1 = Unit) {
        AppEvents.smsReceivedEvent.collect {
            viewModel.refreshSmsMessages()
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
        if (hasPermissions) {
            viewModel.refreshSmsMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SMS Messages") },
                actions = {
                    IconButton(onClick = { viewModel.refreshSmsMessages() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        if (!hasPermissions) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SMS permissions are required to receive and monitor messages.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        permissionLauncher.launch(PermissionUtils.SMS_PERMISSIONS)
                    }
                ) {
                    Text("Grant Permissions")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.selectedFilter == SmsFilter.ALL,
                        onClick = { viewModel.setFilter(SmsFilter.ALL) },
                        label = { Text("All Messages") }
                    )

                    FilterChip(
                        selected = state.selectedFilter == SmsFilter.FILTERED_ONLY,
                        onClick = { viewModel.setFilter(SmsFilter.FILTERED_ONLY) },
                        label = { Text("Filtered Messages") }
                    )
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
                        state.isLoading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }

                        state.error != null -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Error: ${state.error}")
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadSmsMessages() }) {
                                    Text("Retry")
                                }
                            }
                        }

                        state.smsMessages.isEmpty() -> {
                            Text(
                                text = "No SMS messages found",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(state.smsMessages) { sms ->
                                    SmsCard(
                                        sms = sms,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
