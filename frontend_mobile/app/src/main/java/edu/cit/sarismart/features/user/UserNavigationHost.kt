package edu.cit.sarismart.features.user

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.cit.sarismart.features.user.maps.ui.map.UserMapScreen

@Composable
fun UserNavigationHost(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = UserTabs.MAPS.route
    ) {

        composable(UserTabs.MAPS.route) {
            UserMapScreen()
        }
        composable(UserTabs.SASA.route) {
           //
        }
        composable(UserTabs.SCAN.route) {
            //
        }
        composable(UserTabs.STORE.route) {
            //
        }
        composable(UserTabs.ACCOUNT.route) {
            //
        }
    }
}