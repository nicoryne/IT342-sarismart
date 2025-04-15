package edu.cit.sarismart.features.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun UserNavigationController(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(UserTabs.MAPS) }

    Scaffold(
        bottomBar = {
            UserBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            UserNavigationHost (
                navController = navController,
                onLogout = onLogout
            )
        }
    }
}