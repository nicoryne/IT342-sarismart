package edu.cit.sarismart.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.cit.sarismart.ui.auth.LoginScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController();

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen()
        }
    }
}