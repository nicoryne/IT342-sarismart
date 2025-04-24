package edu.cit.sarismart.features.user.tabs.scan.ui.carts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.cit.sarismart.features.user.components.Header

@Composable
fun CartDetailsScreen(
    onNavigateToNotifications: () -> Unit,
    cartId: Long?,
    storeId: Long?,
    viewModel: CartDetailsScreenViewModel = hiltViewModel()
) {
    val cart by viewModel.cart.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Cart Details", onNavigateToNotifications)

        Spacer(modifier = Modifier.height(32.dp))

        if (cartId == null || storeId == null) {
            Text("Error: Invalid Cart or Store ID", color = MaterialTheme.colorScheme.error)
        } else if (cart == null) {
            Text("Loading cart details...")
        } else {
            Text(
                text = cart!!.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Store ID: ${cart!!.storeId}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Items in Cart: ${cart!!.itemCount}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(32.dp))
            Text("TODO: Display list of items here", style = MaterialTheme.typography.bodyLarge)
        }
    }
}