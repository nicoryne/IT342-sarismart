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
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreFormBottomSheetViewModel
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreOverviewScreen

@Composable
fun UserNavigationHost(
    navController: NavHostController,
    onNavigateToLogin: () -> Unit,
    viewModel: UserNavigationHostViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        viewModel.initStores()
    }

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
            val navBackStackEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(UserTabs.STORE.route)
            }
            val showBottomSheet = navBackStackEntry.savedStateHandle.getStateFlow("showBottomSheet", false)
                .collectAsState(initial = false)

            StoreOverviewScreen(
                onNavigateToNotifications = { navController.navigate("notifications") },
                onSelectLocation = { navController.navigate("map_location_selection") },
                showBottomSheet = showBottomSheet,
                onShowBottomSheetChanged = { newValue ->
                    navBackStackEntry.savedStateHandle["showBottomSheet"] = newValue
                }
            )
        }
        composable(UserTabs.ACCOUNT.route) {
            AccountScreen(onNavigateToLogin, onNavigateToNotifications = { navController.navigate("notifications") })
        }

        composable("notifications") {
            NotificationScreen(onGoBack = { navController.navigateUp()})
        }

        composable("map_location_selection") {
            val previousBackStackEntry = remember(navController.currentBackStackEntry) {
                navController.previousBackStackEntry
            }
            val viewModel = previousBackStackEntry?.let {
                hiltViewModel<StoreFormBottomSheetViewModel>(it)
            }

            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(UserTabs.STORE.route)
            }
            val parentShowBottomSheet = remember {
                parentEntry.savedStateHandle.getStateFlow("showBottomSheet", false)
            }.collectAsState(initial = false)

            val parentShowBottomSheetSetter = remember {
                { value: Boolean ->
                    parentEntry.savedStateHandle["showBottomSheet"] = value
                }
            }

            MapLocationSelectionScreen(
                onLocationSelected = { name, latitude, longitude ->
                    viewModel?.onStoreLocationChanged(name)
                    viewModel?.onStoreLatitudeChanged(latitude)
                    viewModel?.onStoreLongitudeChanged(longitude)
                    parentShowBottomSheetSetter(true)
                    navController.popBackStack()
                },
                onCancel = {
                    parentShowBottomSheetSetter(true)
                    navController.popBackStack()
                }
            )
        }
    }
}