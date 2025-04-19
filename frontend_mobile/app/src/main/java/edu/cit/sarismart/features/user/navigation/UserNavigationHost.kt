package edu.cit.sarismart.features.user.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.cit.sarismart.features.user.tabs.account.ui.AccountScreen
import edu.cit.sarismart.features.user.tabs.maps.ui.map.UserMapScreen
import edu.cit.sarismart.features.user.tabs.notifications.ui.NotificationScreen
import edu.cit.sarismart.features.user.tabs.sasa.ui.chat.SasaChatScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.stores.PickStoreScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreOverviewScreen

@Composable
fun UserNavigationHost(
    navController: NavHostController,
    onNavigateToLogin: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = UserTabs.MAPS.route
    ) {

        composable(UserTabs.MAPS.route) {
            UserMapScreen()
        }
        composable(UserTabs.SASA.route) {
            SasaChatScreen(onNavigateToNotifications = { navController.navigate("notifications") })
        }
        composable(UserTabs.SCAN.route) {
            PickStoreScreen(onNavigateToNotifications = { navController.navigate("notifications") })
        }
        composable(UserTabs.STORE.route) {
            StoreOverviewScreen(onNavigateToNotifications = { navController.navigate("notifications") })
        }
        composable(UserTabs.ACCOUNT.route) {
            AccountScreen(onNavigateToLogin, onNavigateToNotifications = { navController.navigate("notifications") })
        }

        composable("notifications") {
            NotificationScreen(onGoBack = { navController.navigateUp()})
        }
    }
}