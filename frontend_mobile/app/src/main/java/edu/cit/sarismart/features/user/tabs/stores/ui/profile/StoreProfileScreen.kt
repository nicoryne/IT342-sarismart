package edu.cit.sarismart.features.user.tabs.stores.ui.profile

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.viewModelScope
import edu.cit.sarismart.features.user.components.Header
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreFormBottomSheet
import kotlinx.coroutines.launch


@Composable
fun StoreProfileScreen (
    viewModel: StoreProfileScreenViewModel = hiltViewModel<StoreProfileScreenViewModel>(),
    storeId: Long?,
    onBack: () -> Unit,
    onSelectLocation: () -> Unit = {}
) {

    var showMenu by remember { mutableStateOf(false) }

    val showEditBottomSheet by viewModel.showEditBottomSheet.collectAsState()
    val showConfirmationDialog by viewModel.showConfirmationDialog.collectAsState()
    val showLoadingDialog by viewModel.showLoadingDialog.collectAsState()
    val showEditLoadingDialog by viewModel.showEditLoadingDialog.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isDeleteLoading by viewModel.isDeleteLoading.collectAsState()
    val isDeleteSuccess by viewModel.isDeleteSuccess.collectAsState()
    val isDeleteError by viewModel.isDeleteError.collectAsState()
    val isEditLoading by viewModel.isEditLoading.collectAsState()
    val isEditSuccess by viewModel.isEditSuccess.collectAsState()
    val isEditError by viewModel.isEditError.collectAsState()
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

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissConfirmationDialog() },
                title = { Text(text = "Confirmation") },
                text = { Text("Are you sure you wish to delete ${store?.storeName}?")},
                confirmButton = {
                    Button(onClick = {
                        viewModel.onDeleteStore()
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        viewModel.onDismissConfirmationDialog()
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showLoadingDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissLoadingDialog() },
                title = {
                    Text("Deleting ${store?.storeName}")
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isDeleteLoading) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Deleting ${store?.storeName}...")
                        } else {
                            isDeleteSuccess?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                            isDeleteError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    }
                },
                confirmButton = {
                    if (!isDeleteLoading) {
                        Button(onClick = { viewModel.onDismissLoadingDialog() }) {
                            Text("Okay")
                        }
                    }
                },
                dismissButton = null
            )
        }

        if (showEditLoadingDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissEditLoadingDialog() },
                title = {
                    Text("Updating ${store?.storeName}")
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isEditLoading) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Updating ${store?.storeName}...")
                        } else {
                            isEditSuccess?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                            isEditError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    }
                },
                confirmButton = {
                    if (!isEditLoading) {
                        Button(onClick = { viewModel.onDismissEditLoadingDialog() }) {
                            Text("Okay")
                        }
                    }
                },
                dismissButton = null
            )
        }

        if (showEditBottomSheet) {
            // Custom StoreFormBottomSheet for editing
            EditStoreBottomSheet(
                viewModel = viewModel,
                onDismissRequest = { viewModel.onDismissEditBottomSheet() },
                onSelectLocation = onSelectLocation,
                onSubmit = { viewModel.onSubmitEdit() }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Go Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = store?.storeName ?: "Store Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Store") },
                        onClick = {
                            viewModel.onShowEditBottomSheet()
                            showMenu = false
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    DropdownMenuItem(
                        text = { Text("Delete Store") },
                        onClick = { viewModel.onShowConfirmationDialog(); showMenu = false }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
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
fun EditStoreBottomSheet(
    viewModel: StoreProfileScreenViewModel,
    onDismissRequest: () -> Unit,
    onSelectLocation: () -> Unit,
    onSubmit: () -> Unit
) {
    val storeName by viewModel.storeName.collectAsState()
    val storeLocation by viewModel.storeLocation.collectAsState()
    val storeNameError by viewModel.storeNameError.collectAsState()
    val storeLocationError by viewModel.storeLocationError.collectAsState()
    val store by viewModel.store.collectAsState()

    StoreFormBottomSheet(
        onDismissRequest = onDismissRequest,
        onSelectLocation = onSelectLocation,
        onSubmitLoading = { /* this will be handled by onSubmit */ },
        onSubmitSuccess = { /* this will be handled by onSubmit */ },
        onSubmitError = { /* this will be handled by onSubmit */ },
        store = store,
        customStoreName = storeName,
        customStoreLocation = storeLocation,
        customStoreNameError = storeNameError,
        customStoreLocationError = storeLocationError,
        onStoreNameChange = { viewModel.updateStoreName(it) },
        onStoreLocationChange = { viewModel.updateStoreLocation(it) },
        onCustomSubmit = onSubmit
    )
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
            onClick = { /* handle Invite Workers */ },
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