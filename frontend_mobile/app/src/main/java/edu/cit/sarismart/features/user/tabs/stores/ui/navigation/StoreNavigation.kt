package edu.cit.sarismart.features.user.tabs.stores.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edu.cit.sarismart.features.user.tabs.stores.ui.products.AddProductScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.products.EditProductScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.products.ProductDetailScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.products.ProductListScreen
import edu.cit.sarismart.features.user.tabs.stores.ui.profile.StoreProfileScreen

object StoreRoutes {
    const val STORE_PROFILE = "store_profile/{storeId}"
    const val PRODUCT_LIST = "product_list/{storeId}"
    const val PRODUCT_DETAIL = "product_detail/{storeId}/{productId}"
    const val ADD_PRODUCT = "add_product/{storeId}"
    const val EDIT_PRODUCT = "edit_product/{storeId}/{productId}"

    fun storeProfile(storeId: Long) = "store_profile/$storeId"
    fun productList(storeId: Long) = "product_list/$storeId"
    fun productDetail(storeId: Long, productId: Long) = "product_detail/$storeId/$productId"
    fun addProduct(storeId: Long) = "add_product/$storeId"
    fun editProduct(storeId: Long, productId: Long) = "edit_product/$storeId/$productId"
}

@Composable
fun StoreNavigation(
    navController: NavHostController,
    startDestination: String,
    onBack: () -> Unit,
    onSelectLocation: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = StoreRoutes.STORE_PROFILE,
            arguments = listOf(
                navArgument("storeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: 0L
            StoreProfileScreen(
                storeId = storeId,
                onBack = onBack,
                onSelectLocation = onSelectLocation,
                onNavigateToProductDetail = { sId, pId ->
                    navController.navigate(StoreRoutes.productDetail(sId, pId))
                },
                onNavigateToAddProduct = { sId ->
                    navController.navigate(StoreRoutes.addProduct(sId))
                }
            )
        }

        composable(
            route = StoreRoutes.PRODUCT_LIST,
            arguments = listOf(
                navArgument("storeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: 0L
            ProductListScreen(
                storeId = storeId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToAddProduct = { sId ->
                    navController.navigate(StoreRoutes.addProduct(sId))
                },
                onNavigateToProductDetail = { sId, pId ->
                    navController.navigate(StoreRoutes.productDetail(sId, pId))
                }
            )
        }

        composable(
            route = StoreRoutes.PRODUCT_DETAIL,
            arguments = listOf(
                navArgument("storeId") { type = NavType.LongType },
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: 0L
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailScreen(
                storeId = storeId,
                productId = productId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEditProduct = { sId, pId ->
                    navController.navigate(StoreRoutes.editProduct(sId, pId))
                }
            )
        }

        composable(
            route = StoreRoutes.ADD_PRODUCT,
            arguments = listOf(
                navArgument("storeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: 0L
            AddProductScreen(
                storeId = storeId,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = StoreRoutes.EDIT_PRODUCT,
            arguments = listOf(
                navArgument("storeId") { type = NavType.LongType },
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val storeId = backStackEntry.arguments?.getLong("storeId") ?: 0L
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            EditProductScreen(
                storeId = storeId,
                productId = productId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}