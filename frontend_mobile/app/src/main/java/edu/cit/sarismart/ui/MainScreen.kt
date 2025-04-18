package edu.cit.sarismart.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.cit.sarismart.ui.auth.LoginScreen
import edu.cit.sarismart.ui.auth.RegisterScreen
import edu.cit.sarismart.ui.guest.GuestMapScreen
import edu.cit.sarismart.ui.onboarding.OnboardingScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            StartScreen(navController, viewModel)
        }

        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    viewModel.completeOnboarding()
                    navController.navigate("login") {
                        popUpTo("start") { inclusive = true }
                    }
                },
                onSkip = {
                    viewModel.completeOnboarding()
                    navController.navigate("login") {
                        popUpTo("start") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onCreateAccountClick = { navController.navigate("register") },
                onForgotPasswordClick = { /* TODO: Navigate to forgot password */ },
                onNavigateToGuest = { navController.navigate("guest") },
                onNavigateToHome = { /* TODO: Navigate to user home */}
            )
        }

        composable("register") {
            RegisterScreen(
                onLoginClick = { navController.navigateUp() },
                onSuccessfulRegistration = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("guest") {
            GuestMapScreen(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
    }
}

@Composable
fun StartScreen(navController: NavHostController, viewModel: MainViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }

    LaunchedEffect(key1 = true) {
        viewModel.checkOnboardingStatus()

        viewModel.onboardingCompleted.collectLatest { completed ->
            if (completed) {
                navController.navigate("login") {
                    popUpTo("start") { inclusive = true }
                }
            } else {
                navController.navigate("onboarding") {
                    popUpTo("start") { inclusive = true }
                }
            }
        }
    }
}