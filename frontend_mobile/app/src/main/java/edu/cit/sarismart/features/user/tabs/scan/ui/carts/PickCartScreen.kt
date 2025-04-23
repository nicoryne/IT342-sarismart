package edu.cit.sarismart.features.user.tabs.scan.ui.carts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edu.cit.sarismart.features.user.components.Header
import edu.cit.sarismart.features.user.tabs.scan.data.models.Cart
import kotlinx.coroutines.launch

@Composable
fun PickCartScreen(
    onNavigateToNotifications: () -> Unit,
    onCartSelected: (Cart) -> Unit,
    storeId: Long,
    viewModel: PickCartViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val carts by viewModel.carts.collectAsState()

    LaunchedEffect(key1 = storeId) {
        viewModel.setStoreId(storeId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Pick a Cart", onNavigateToNotifications)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { viewModel.viewModelScope.launch { viewModel.createNewCart() } }) {
            Icon(Icons.Filled.Add, contentDescription = "Create New Cart")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New Cart")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (carts.isEmpty()) {
            Text("No carts available for this store. Create one!")
        } else {
            LazyColumn {
                items(carts) { cart ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate("cart_details/$storeId/${cart.id}")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = cart.name)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "${cart.itemCount} items")
                    }
                }
            }
        }
    }
}