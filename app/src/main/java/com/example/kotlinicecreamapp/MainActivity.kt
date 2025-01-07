package com.example.kotlinicecreamapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kotlinicecreamapp.core.NotificationHelper
import com.example.kotlinicecreamapp.core.OfflineSyncWorker
import com.example.kotlinicecreamapp.core.TAG
import com.example.kotlinicecreamapp.ui.theme.KotlinIceCreamAppTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    //    private lateinit var networkMonitor: ConnectivityManagerNetworkMonitor
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val networkMonitor = (application as MyApplication).container.networkMonitor
        setContent {
            Log.d(TAG, "onCreate")
            MyApp {
                MyAppNavHost()
            }
        }

        notificationHelper = NotificationHelper(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!notificationHelper.hasNotificationPermission()) {
                requestNotificationPermission()
            }
        }

        lifecycleScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                handleNetworkChange(isOnline)
            }
        }
    }

    private fun handleNetworkChange(isOnline: Boolean) {
        if (isOnline) {
            Log.d(TAG, "Network is online")
//            notificationHelper.showNetworkStatusNotification(true)
            triggerUploadWorker()
        } else {
            Log.d(TAG, "Network is offline")
            notificationHelper.showNetworkStatusNotification(false)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

//    /**
//     * Handle permission request result
//     */
//    @Deprecated("Deprecated in Java")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                if (grantResults.isNotEmpty() &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED
//                ) {
//                    Log.d("MainActivity", "Notification permission granted")
//                } else {
//                    Log.d("MainActivity", "Notification permission denied")
//                }
//            }
//        }
//    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

    private fun triggerUploadWorker() {
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<OfflineSyncWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            (application as MyApplication).container.iceCreamRepository.openWsClient()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            (application as MyApplication).container.iceCreamRepository.closeWsClient()
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    Log.d("MyApp", "recompose")
    KotlinIceCreamAppTheme() {
        Surface {
            content()
        }
    }
}

@Preview
@Composable
fun PreviewMyApp() {
    MyApp {
        MyAppNavHost()
    }
}