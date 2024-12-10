package com.example.kotlinicecreamapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kotlinicecreamapp.core.OfflineSyncWorker
import com.example.kotlinicecreamapp.core.TAG
import com.example.kotlinicecreamapp.ui.theme.KotlinIceCreamAppTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
//    private lateinit var networkMonitor: ConnectivityManagerNetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val networkMonitor = (application as MyApplication).container.networkMonitor
        setContent {
            Log.d(TAG, "onCreate")
            MyApp {
                MyAppNavHost()
            }
        }

        lifecycleScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                handleNetworkChange(isOnline)
            }
        }
    }

    private fun handleNetworkChange(isOnline: Boolean) {
        if(isOnline) {
            Log.d(TAG, "Network is online")
            triggerUploadWorker()
        } else {
            Log.d(TAG, "Network is offline")
//            showNotification("No internet connection")
        }
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

//    fun showNotification(message: String) {
//        val notificationManager = NotificationManagerCompat.from(this)
//        val notification = NotificationCompat.Builder(this, "ice_cream_channel")
//            .setContentTitle("IceCream App")
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        with(notificationManager) {
//            notify(System.currentTimeMillis().toInt(), notification)
//        }
//    }


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