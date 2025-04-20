package com.emrepbu.smsgateway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.emrepbu.smsgateway.ui.navigation.SmsGatewayNavHost
import com.emrepbu.smsgateway.ui.theme.SMSGatewayTheme
import com.emrepbu.smsgateway.utils.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // TODO
            // All permissions granted, proceed with app functionality
            // You might want to refresh SMS messages here
        } else {
            showPermissionDeniedMessage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkSmsPermissions()

        setContent {
            SMSGatewayTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    SmsGatewayNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkSmsPermissions() {
        if (!PermissionUtils.hasSmsPermissions(this)) {
            requestPermissionLauncher.launch(PermissionUtils.SMS_PERMISSIONS)
        }
    }

    private fun showPermissionDeniedMessage() {
        lifecycleScope.launch {
            // You could show a dialog here explaining why the permissions are needed
            // For now, let's just log the message
            android.util.Log.w("SMSGateway", "SMS permissions denied by user")
        }
    }
}