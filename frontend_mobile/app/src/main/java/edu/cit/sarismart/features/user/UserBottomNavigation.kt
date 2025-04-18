package edu.cit.sarismart.features.user

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController

@Composable
fun UserBottomNavigation(
    selectedTab: UserTabs,
    onTabSelected: (UserTabs) -> Unit,
    navController: NavHostController
) {
    NavigationBar {
        UserTabs.entries.forEach { tab ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = tab.iconResId),

                        contentDescription = tab.title,
                        tint = if (selectedTab == tab)
                            Color(0xFF009393)
                        else
                            Color(0xFF4F4F4F)
                    )
                },
                label = { Text(tab.title) },
                selected = selectedTab == tab,
                onClick = {
                    if (selectedTab != tab) {
                        onTabSelected(tab)
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
