package com.example.kotlinicecreamapp

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kotlinicecreamapp.auth.LoginScreen
import com.example.kotlinicecreamapp.core.data.UserPreferences
import com.example.kotlinicecreamapp.core.data.remote.Api
import com.example.kotlinicecreamapp.core.ui.UserPreferencesViewModel
import com.example.kotlinicecreamapp.todo.ui.iceCream.IceCreamScreen
import com.example.kotlinicecreamapp.todo.ui.iceCreams.IceCreamsScreen
import com.example.kotlinicecreamapp.util.ConnectivityManagerNetworkMonitor

val iceCreamsRoute = "icecreams"
val authRoute = "auth"

@Composable
fun MyAppNavHost() {
    val navController = rememberNavController()
    val onCloseItem = {
        Log.d("MyAppNavHost", "navigate back to list")
        navController.popBackStack()
    }
    val userPreferencesViewModel =
        viewModel<UserPreferencesViewModel>(factory = UserPreferencesViewModel.Factory)
    val userPreferencesUIState by userPreferencesViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = UserPreferences()
    )
    val myAppViewModel = viewModel<MyAppViewModel>(factory = MyAppViewModel.Factory)
    NavHost(
        navController = navController,
        startDestination = authRoute
    ) {
        composable(iceCreamsRoute) {
            IceCreamsScreen(
                onIceCreamClick = { iceCreamId ->
                    Log.d("MyAppNavHost", "navigate to icecream $iceCreamId")
                    navController.navigate("$iceCreamsRoute/$iceCreamId")
                },
                onAddItem = {
                    Log.d("MyAppNavHost", "navigate to new icecream")
                    navController.navigate("$iceCreamsRoute-new")
                },
                onLogout = {
                    Log.d("MyAppNavHost", "logout")
                    myAppViewModel.logout()
                    Api.tokenInterceptor.token = null
                    navController.navigate(authRoute) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(
            route = "$iceCreamsRoute/{iceCreamId}",
            arguments = listOf(navArgument("iceCreamId") { type = NavType.StringType })
        ) {
            IceCreamScreen(
                iceCreamId = it.arguments?.getString("iceCreamId"),
                onClose = { onCloseItem() }
            )
        }
        composable(
            route = "$iceCreamsRoute-new"
        ) {
            IceCreamScreen(
                iceCreamId = null,
                onClose = {
                    onCloseItem()
                }
            )
        }
        composable(
            route = authRoute
        ) {
            LoginScreen(
                onClose = {
                    Log.d("MyAppNavHost", "navigate to list")
                    navController.navigate(iceCreamsRoute)
                }
            )
        }
    }
    LaunchedEffect(userPreferencesUIState.token) {
        if (userPreferencesUIState.token.isNotEmpty()) {
            Log.d("MyAppNavHost", "Launched effect navigate to icecreams")
            Api.tokenInterceptor.token = userPreferencesUIState.token
            myAppViewModel.setToken(userPreferencesUIState.token)
            navController.navigate(iceCreamsRoute) {
                popUpTo(0)
            }
        }
    }
}