package com.emrepbu.smsgateway.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emrepbu.smsgateway.ui.components.FilterRuleCard
import com.emrepbu.smsgateway.ui.viewmodel.FilterRuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterRuleListScreen(
    viewModel: FilterRuleViewModel = hiltViewModel(),
    onNavigateToFilterRuleDetail: (Long) -> Unit,
    onNavigateToAddRule: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filter Rules") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddRule) {
                Icon(Icons.Default.Add, contentDescription = "Add Filter Rule")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                        Button(onClick = { viewModel.loadFilterRules() }) {
                            Text("Retry")
                        }
                    }
                }

                state.filterRules.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No filter rules found")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateToAddRule) {
                            Text("Create Your First Rule")
                        }
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.filterRules) { rule ->
                            FilterRuleCard(
                                rule = rule,
                                onClick = { onNavigateToFilterRuleDetail(rule.id) },
                                onToggleEnabled = {
                                    viewModel.toggleRuleEnabled(
                                        rule.id,
                                        !rule.isEnabled
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
