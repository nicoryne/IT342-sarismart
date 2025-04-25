package edu.cit.sarismart.features.user.tabs.scan.ui.stores

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edu.cit.sarismart.features.user.components.Header
import edu.cit.sarismart.features.user.tabs.scan.ui.components.StorePickItem
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreOverviewScreenViewModel

@Composable
fun PickStoreScreen(
    onNavigateToNotifications: () -> Unit,
    viewModel: StoreOverviewScreenViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val allStores by viewModel.stores.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val filteredStores = remember(searchText, allStores) {
        if (searchText.isBlank()) {
            allStores
        } else {
            allStores.filter { it.storeName.contains(searchText, ignoreCase = true) }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.getStores()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Header("Pick a Store", onNavigateToNotifications)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search Store") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
            items(filteredStores) { store ->
                StorePickItem(
                    onStoreItemClick = { storeId ->
                        navController.navigate("store_menu/$storeId")
                    },
                    storeId = store.id,
                    storeName = store.storeName,
                    storeLocation = store.location,
                    isOwner = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}