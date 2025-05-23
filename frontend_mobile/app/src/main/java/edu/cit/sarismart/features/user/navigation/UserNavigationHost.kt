package edu.cit.sarismart.features.user.navigation

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edu.cit.sarismart.features.user.tabs.account.ui.AccountScreen
import edu.cit.sarismart.features.user.tabs.maps.ui.map.UserMapScreen
import edu.cit.sarismart.features.user.tabs.notifications.ui.NotificationScreen
import edu.cit.sarismart.features.user.tabs.sasa.ui.chat.SasaChatScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.carts.CartDetailsScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.carts.PickCartScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.carts.CartOverviewScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.inventory.InventoryScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.sales.SalesScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.scanner.BarcodeScannerScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.settings.StoreSettingsScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.stores.PickStoreScreen
import edu.cit.sarismart.features.user.tabs.scan.ui.stores.StoreMenuScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.MapLocationSelectionScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreOverviewScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreOverviewScreenViewModel
import edu.cit.sarismart.features.user.tabs.stores.ui.products.AddProductScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.products.EditProductScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.products.ProductDetailScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.products.ProductListScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.profile.StoreProfileScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.profile.StoreProfileScreenViewModel

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
            PickStoreScreen(
                onNavigateToNotifications = { navController.navigate("notifications") },
                navController = navController
            )
        }
        composable(UserTabs.STORE.route) {
            StoreOverviewScreen(
                onNavigateToNotifications = { navController.navigate("notifications") },
                onSelectLocation = { navController.navigate("map_location_selection") },
                onNavigateToProfile = {
                        storeId -> navController.navigate("store_profile/$storeId");
                    Log.d("UserNavigationHost", "Navigating to store profile with ID: $storeId")
                }
            )
        }
        composable("store_profile/{storeId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLongOrNull()
            Log.d("UserNavigationHost", "Store ID: $storeId")
            StoreProfileScreen(
                storeId = storeId,
                onBack = { navController.popBackStack() },
                onSelectLocation = { navController.navigate("map_location_selection_profile") },
                onNavigateToProductDetail = { sId, pId ->
                    navController.navigate("store/$sId/products/$pId")
                },
                onNavigateToAddProduct = { sId ->
                    navController.navigate("store/$sId/products/add")
                }
            )
        }

        composable(
            route = "store/{storeId}/products",
            arguments = listOf(navArgument("storeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: -1L

            ProductListScreen(
                storeId = storeId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddProduct = { sId ->
                    navController.navigate("store/$sId/products/add")
                },
                onNavigateToProductDetail = { sId, pId ->
                    navController.navigate("store/$sId/products/$pId")
                }
            )
        }

        composable(
            route = "store/{storeId}/products/{productId}",
            arguments = listOf(
                navArgument("storeId") { type = NavType.LongType },
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: -1L
            val productId = backStackEntry.arguments?.getLong("productId") ?: -1L

            ProductDetailScreen(
                storeId = storeId,
                productId = productId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditProduct = { sId, pId ->
                    navController.navigate("store/$sId/products/$pId/edit")
                }
            )
        }

        composable(
            route = "store/{storeId}/products/add",
            arguments = listOf(navArgument("storeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: -1L

            AddProductScreen(
                storeId = storeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "store/{storeId}/products/{productId}/edit",
            arguments = listOf(
                navArgument("storeId") { type = NavType.LongType },
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: -1L
            val productId = backStackEntry.arguments?.getLong("productId") ?: -1L

            EditProductScreen(
                storeId = storeId,
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(UserTabs.ACCOUNT.route) {
            AccountScreen(
                onNavigateToLogin = { onNavigateToLogin() },
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

        composable("map_location_selection_profile") {
            val previousBackStackEntry = remember(navController.currentBackStackEntry) {
                navController.previousBackStackEntry
            }

            val viewModel = previousBackStackEntry?.let {
                hiltViewModel<StoreProfileScreenViewModel>(it)
            }

            MapLocationSelectionScreen(
                onLocationSelected = { name, latitude, longitude ->
                    viewModel?.updateStoreLocation(name)
                    viewModel?.updateStoreLatitude(latitude)
                    viewModel?.updateStoreLongitude(longitude)
                    navController.popBackStack()
                },
                onNavigateToStore = { navController.popBackStack() }
            )
        }

        composable("pick_store") {
            PickStoreScreen(
                onNavigateToNotifications = { navController.navigate("notifications") },
                navController = navController
            )
        }

        composable("store_menu/{storeId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLongOrNull()

            StoreMenuScreen(
                onNavigateToNotifications = { navController.navigate("notifications") },
                navController = navController,
                onNavigateToScanner = { storeId, cartId ->
                    navController.navigate("barcode_scanner/$storeId/$cartId")
                },
                storeId = storeId
            )
        }

        composable("cart_details/{storeId}/{cartId}") { backStackEntry ->
            val cartId = backStackEntry.arguments?.getString("cartId")?.toLongOrNull()
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLongOrNull()
            CartDetailsScreen(
                onNavigateToNotifications = { navController.navigate("notifications") },
                cartId = cartId,
                storeId = storeId
            )
        }

        // New screens for the scan feature
        composable("barcode_scanner/{storeId}/{cartId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLongOrNull() ?: -1L
            val cartId = backStackEntry.arguments?.getString("cartId")?.toLongOrNull() ?: -1L

            BarcodeScannerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCartOverview = { storeId, cartId ->
                    navController.navigate("cart_overview/$storeId/$cartId") {
                        // Pop up to the store menu so back navigation works properly
                        popUpTo("store_menu/$storeId") {
                            inclusive = false
                        }
                    }
                },
                storeId = storeId,
                cartId = cartId
            )
        }

        composable("cart_overview/{storeId}/{cartId}") { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLongOrNull() ?: -1L
            val cartId = backStackEntry.arguments?.getString("cartId")?.toLongOrNull() ?: -1L

            CartOverviewScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScanner = { storeId, cartId ->
                    navController.navigate("barcode_scanner/$storeId/$cartId")
                },
                onCheckoutComplete = {
                    // Navigate back to store menu after checkout
                    navController.navigate("store_menu/$storeId") {
                        popUpTo("store_menu/$storeId") {
                            inclusive = true
                        }
                    }
                },
                storeId = storeId,
                cartId = cartId
            )
        }

        composable(
            route = "store/{storeId}/scan",
            arguments = listOf(navArgument("storeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: -1L

            // Log the storeId to verify it's being passed correctly
            Log.d("Navigation", "Navigating to PickCartScreen with storeId: $storeId")

            PickCartScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScanner = { storeId, cartId ->
                    navController.navigate("barcode_scanner/$storeId/$cartId")
                }
            )
        }

        composable(
            route = "store/{storeId}/inventory",
            arguments = listOf(navArgument("storeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLongOrNull() ?: -1L

            // Log the storeId to verify it's being passed correctly
            Log.d("Navigation", "Navigating to InventoryScreen with storeId: $storeId")

            InventoryScreen(
                onNavigateBack = { navController.popBackStack() },
                storeId = storeId
            )
        }

        composable(
            route = "store/{storeId}/sales",
            arguments = listOf(navArgument("storeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLongOrNull() ?: -1L

            // Log the storeId to verify it's being passed correctly
            Log.d("Navigation", "Navigating to SalesScreen with storeId: $storeId")

            SalesScreen(
                onNavigateBack = { navController.popBackStack() },
                storeId = storeId
            )
        }

        composable(
            route = "store/{storeId}/settings",
            arguments = listOf(navArgument("storeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLongOrNull() ?: -1L

            // Log the storeId to verify it's being passed correctly
            Log.d("Navigation", "Navigating to StoreSettingsScreen with storeId: $storeId")

            StoreSettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                storeId = storeId
            )
        }
    }
}