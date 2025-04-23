package edu.cit.sarismart.features.user.tabs.stores.ui.profile

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import edu.cit.sarismart.features.user.components.Header
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store


@Composable
fun StoreProfileScreen (
    viewModel: StoreProfileScreenViewModel = hiltViewModel<StoreProfileScreenViewModel>(),
    storeId: Long?,
    onBack: () -> Unit,
    onNotificationClick: () -> Unit
) {

    val isLoading by viewModel.isLoading.collectAsState()
    val store by viewModel.store.collectAsState()

    LaunchedEffect(key1 = true) {
        Log.d("StoreProfileScreen", "Fetching store with ID: $storeId")
        if (storeId != null) {
            viewModel.fetchStore(storeId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(title = store?.storeName ?: "Store Profile", onBackTrack = { onBack() }, hasBackTrack = true, onNotificationClick = { onNotificationClick() })

        Spacer(modifier = Modifier.height(8.dp))

        Row (
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = "Location")
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = store?.location ?: "Store Location",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            store?.let { StoreContent(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreContent(store: Store) {
    var searchText by remember { mutableStateOf("") }

    val sampleProducts = listOf(
        Product(
            id = 1,
            name = "Laptop",
            category = "Electronics",
            price = 1200.00,
            stock = 10,
            description = "High-performance laptop with SSD.",
            reorderLevel = 3,
            store = store
        ),
        Product(
            id = 2,
            name = "Wireless Mouse",
            category = "Electronics",
            price = 25.00,
            stock = 50,
            description = "Ergonomic wireless mouse.",
            reorderLevel = 10,
            store = store
        ),
        Product(
            id = 3,
            name = "Office Chair",
            category = "Furniture",
            price = 150.00,
            stock = 5,
            description = "Comfortable office chair.",
            reorderLevel = 2,
            store = store
        ),
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            placeholder = { Text("Search Products") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(sampleProducts) { product ->
                ProductItem(product = product)
            }
        }
        Button(
            onClick = { /* Handle Invite Workers */ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add User")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Invite Workers")
            }

        }
    }
}

