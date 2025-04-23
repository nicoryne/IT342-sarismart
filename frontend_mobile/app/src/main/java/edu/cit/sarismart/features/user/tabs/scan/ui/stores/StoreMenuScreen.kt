package edu.cit.sarismart.features.user.tabs.scan.ui.stores

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edu.cit.sarismart.features.user.components.Header

@Composable
fun StoreMenuScreen(
    onNavigateToNotifications: () -> Unit,
    storeId: Long?,
    navController: NavHostController = rememberNavController()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Store Menu", onNavigateToNotifications)

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Store ID: $storeId", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium) // Optional: Display store ID

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            if (storeId != null) {
                navController.navigate("pick_cart/$storeId")
            }
        }) {
            Text("Pick a Cart")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (storeId != null) {
                navController.navigate("pick_cart/$storeId")
            }
        }) {
            Text("Create a Cart")
        }
    }
}