package edu.cit.sarismart.features.user.tabs.stores.ui.profile

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import edu.cit.sarismart.features.user.tabs.stores.ui.components.InviteWorkersButton
import edu.cit.sarismart.features.user.tabs.stores.ui.products.ProductListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreProfileScreen(
    viewModel: StoreProfileScreenViewModel = hiltViewModel(),
    productListViewModel: ProductListViewModel = hiltViewModel(),
    storeId: Long?,
    onBack: () -> Unit,
    onSelectLocation: () -> Unit = {},
    onNavigateToProductDetail: (Long, Long) -> Unit,
    onNavigateToAddProduct: (Long) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }

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

    val products by productListViewModel.products.collectAsState()
    val productsLoading by productListViewModel.isLoading.collectAsState()
    val searchQuery by productListViewModel.searchQuery.collectAsState()

    LaunchedEffect(key1 = true) {
        Log.d("StoreProfileScreen", "Fetching store with ID: $storeId")
        if (storeId != null) {
            viewModel.fetchStore(storeId)
            productListViewModel.loadProducts(storeId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dialogs and bottom sheets remain the same
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
            EditStoreBottomSheet(
                viewModel = viewModel,
                onDismissRequest = { viewModel.onDismissEditBottomSheet() },
                onSelectLocation = onSelectLocation,
                onSubmit = { viewModel.onSubmitEdit() }
            )
        }

        if (showInviteDialog) {
            InviteWorkersDialog(
                viewModel = viewModel,
                onDismissRequest = { showInviteDialog = false }
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
            store?.let {
                StoreContent(
                    it,
                    products = products,
                    isLoading = productsLoading,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { productListViewModel.updateSearchQuery(it) },
                    onProductClick = { productId ->
                        storeId?.let { sId -> onNavigateToProductDetail(sId, productId) }
                    },
                    onAddProductClick = {
                        storeId?.let { sId -> onNavigateToAddProduct(sId) }
                    },
                    onInviteWorkersClick = {
                        storeId?.let { sId ->
                            viewModel.generateInvitationCode(sId.toString())
                            showInviteDialog = true
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreContent(
    store: Store,
    products: List<Product>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onProductClick: (Long) -> Unit,
    onAddProductClick: () -> Unit,
    onInviteWorkersClick: () -> Unit,
    productListViewModel: ProductListViewModel = hiltViewModel()
) {
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    val isDeleting by productListViewModel.isDeleting.collectAsState()
    val deleteSuccess by productListViewModel.deleteSuccess.collectAsState()
    val errorMessage by productListViewModel.errorMessage.collectAsState()

    // Show delete confirmation dialog
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product") },
            text = {
                Text("Are you sure you want to delete ${product.name}? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        productListViewModel.deleteProduct(store.id, product.id)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onError,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { productToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Show success snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(deleteSuccess) {
        deleteSuccess?.let { product ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "${product.name} has been deleted",
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
                productListViewModel.resetDeleteState()
            }
        }
    }

    // Show error snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
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

            // Products list
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products found. Add your first product!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(products) { product ->
                        ProductItem(
                            product = product,
                            onProductClick = { onProductClick(it.id) },
                            onEditClick = { onProductClick(it.id) },
                            onDeleteClick = { productToDelete = it }
                        )
                    }
                }
            }

            // Add product button
            Button(
                onClick = { onAddProductClick() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Product")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add New Product")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            InviteWorkersButton(
                onInviteWorkersClick = onInviteWorkersClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
