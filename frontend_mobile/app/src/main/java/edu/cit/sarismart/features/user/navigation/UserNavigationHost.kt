package edu.cit.sarismart.features.user.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.cit.sarismart.features.user.tabs.account.ui.AccountScreen
import edu.cit.sarismart.features.user.tabs.maps.ui.map.UserMapScreen
import edu.cit.sarismart.features.user.tabs.notifications.ui.NotificationScreen
import edu.cit.sarismart.features.user.tabs.sasa.ui.chat.SasaChatScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.stores.PickStoreScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.MapLocationSelectionScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreOverviewScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreOverviewScreenViewModel

@Composable
fun UserNavigationHost(
    navController: NavHostController,
    onNavigateToLogin: () -> Unit,
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
            StoreOverviewScreen(
                onNavigateToNotifications = { navController.navigate("notifications") },
                onSelectLocation = { navController.navigate("map_location_selection") }
            )
        }

        composable(UserTabs.ACCOUNT.route) {
            AccountScreen(
                onNavigateToLogin,
                onClearBackStack = { navController.clearBackStack(UserTabs.ACCOUNT.route) },
                onNavigateToNotifications = { navController.navigate("notifications") },
            )
        }

        composable("notifications") {
            NotificationScreen(onGoBack = { navController.navigateUp()})
        }

        composable("map_location_selection") {
            val previousBackStackEntry = remember(navController.currentBackStackEntry) {
                navController.previousBackStackEntry
            }

            val viewModel = previousBackStackEntry?.let {
                hiltViewModel<StoreOverviewScreenViewModel>(it)
            }

            MapLocationSelectionScreen(
                onLocationSelected = { name, latitude, longitude ->
                    viewModel?.updateStoreLocation(name)
                    viewModel?.updateStoreLatitude(latitude)
                    viewModel?.updateStoreLongitude(longitude)
                    navController.popBackStack()
                },
                onNavigateToStore = { navController.navigate("store") }
            )
        }
    }
}