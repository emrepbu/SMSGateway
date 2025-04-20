package com.emrepbu.smsgateway.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Utility object for managing SMS permissions.
 *
 * This object provides constants for SMS permissions and a function to check if those
 * permissions have been granted.
 */
object PermissionUtils {
    
    val SMS_PERMISSIONS = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS
    )
    
    const val SMS_PERMISSION_REQUEST_CODE = 100

    fun hasSmsPermissions(context: Context): Boolean {
        return SMS_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
