package edu.cit.sarismart.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.cit.sarismart.features.auth.ui.forgotpassword.ForgotPasswordScreen
import edu.cit.sarismart.features.auth.ui.login.LoginScreen
import edu.cit.sarismart.features.auth.ui.register.RegisterScreen
import edu.cit.sarismart.features.guest.ui.GuestMapScreen
import edu.cit.sarismart.features.onboarding.ui.OnboardingScreen
import edu.cit.sarismart.features.user.navigation.UserNavigationController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CoreNavigationController(viewModel: CoreNavigationViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "start") {

        // Start
        composable("start") {
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

        // Onboarding
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

        // Login
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onNavigateToGuest = { navController.navigate("guest") },
                onNavigateToHome = { navController.navigate("user")}
            )
        }

        // Register
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigateUp() },
                onSuccessfulRegistration = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Forgot Password
        composable("forgot_password") {
            ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Guest
        composable("guest") {
            GuestMapScreen(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        // User
        composable("user") {
            UserNavigationController(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

    }
}