package edu.cit.sarismart.features.user.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun UserBottomNavigation(
    selectedTab: UserTabs,
    onTabSelected: (UserTabs) -> Unit,
    navController: NavHostController
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 10.dp,
        color = Color.White,
        modifier = Modifier.shadow(10.dp)
    ) {
        NavigationBar(
            containerColor = Color.Transparent
        ) {
            UserTabs.entries.forEach { tab ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = tab.iconResId),
                            contentDescription = tab.title,
                        )
                    },
                    label = { Text(text = tab.title) },
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
                    },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF4F4F4F),
                        unselectedTextColor = Color(0xFF4F4F4F),
                        selectedIconColor = Color(0xFF009393),
                        selectedTextColor = Color(0xFF009393),
                        indicatorColor = Color.White
                    )
                )
            }
        }
    }
}
