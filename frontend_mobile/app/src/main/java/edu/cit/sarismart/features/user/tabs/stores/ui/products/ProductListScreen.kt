package edu.cit.sarismart.features.user.tabs.stores.ui.products

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.DismissDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.ui.profile.ProductItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProductListScreen(
    storeId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToAddProduct: (Long) -> Unit,
    onNavigateToProductDetail: (Long, Long) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val products by viewModel.products.collectAsState()
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(storeId) {
        viewModel.loadProducts(storeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToAddProduct(storeId) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Product"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                placeholder = { Text("Search products") },
                singleLine = true
            )

            if (isLoading && products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                val filteredProducts = viewModel.getFilteredProducts()

                if (filteredProducts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No products match your search",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = filteredProducts,
                            key = { it.id }
                        ) { product ->
                            val dismissState = rememberDismissState(
                                confirmStateChange = {
                                    if (it == DismissValue.DismissedToStart) {
                                        productToDelete = product
                                        showDeleteDialog = true
                                    }
                                    false
                                }
                            )

                            SwipeToDismiss(
                                state = dismissState,
                                background = {
                                    val color by animateColorAsState(
                                        targetValue = when (dismissState.dismissDirection) {
                                            DismissDirection.EndToStart -> Color.Red.copy(alpha = 0.2f)
                                            else -> Color.Transparent
                                        }
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 16.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.Red
                                        )
                                    }
                                },
                                dismissContent = {
                                    ProductItem(
                                        product = product,
                                        onProductClick = {
                                            onNavigateToProductDetail(
                                                storeId,
                                                product.id
                                            )
                                        },
                                        onEditClick = {
                                            onNavigateToProductDetail(
                                                storeId,
                                                product.id
                                            )
                                        },
                                        onDeleteClick = { productToDelete = it }
                                    )
                                },
                                directions = setOf(DismissDirection.EndToStart)
                            )
                        }
                    }
                }
            }

            if (errorMessage != null) {
                AlertDialog(
                    onDismissRequest = { /* Do nothing */ },
                    title = { Text("Error") },
                    text = { Text(errorMessage ?: "Unknown error occurred") },
                    confirmButton = {
                        Button(
                            onClick = { scope.launch { viewModel.loadProducts(storeId) } }
                        ) {
                            Text("Retry")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { /* Dismiss */ }
                        ) {
                            Text("Dismiss")
                        }
                    }
                )
            }

            if (showDeleteDialog) {
                val isDeleting by viewModel.isDeleting.collectAsState(initial = false)

                AlertDialog(
                    onDismissRequest = {
                        if (!isDeleting) showDeleteDialog = false
                    },
                    title = { Text("Delete Product") },
                    text = {
                        Column {
                            Text("Are you sure you want to delete ${productToDelete?.name}?")
                            Text(
                                "This action cannot be undone.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                productToDelete?.let {
                                    viewModel.deleteProduct(storeId, it.id)
                                    showDeleteDialog = false
                                }
                            },
                            enabled = !isDeleting,
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
                        TextButton(
                            onClick = { showDeleteDialog = false },
                            enabled = !isDeleting
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}