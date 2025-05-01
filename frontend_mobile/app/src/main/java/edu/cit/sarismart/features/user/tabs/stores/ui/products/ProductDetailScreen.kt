package edu.cit.sarismart.features.user.tabs.stores.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    storeId: Long,
    productId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEditProduct: (Long, Long) -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
        val isLoading by viewModel.isLoading.collectAsState()
        val product by viewModel.product.collectAsState()
        val errorMessage by viewModel.errorMessage.collectAsState()
        val scope = rememberCoroutineScope()

        var showStockDialog by remember { mutableStateOf(false) }
        var newStockValue by remember { mutableStateOf("") }
        var stockError by remember { mutableStateOf<String?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(product?.name ?: "Product Details") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            product?.let { onNavigateToEditProduct(storeId, it.id) }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Product"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (product == null && errorMessage == null) {
                    Text(
                        text = "Product not found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else if (errorMessage != null) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Unknown error occurred",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* Retry loading */ }
                        ) {
                            Text("Retry")
                        }
                    }
                } else {
                    product?.let { currentProduct ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = currentProduct.name,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Price: $${currentProduct.price}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Button(
                                            onClick = { showStockDialog = true }
                                        ) {
                                            Text("Update Stock")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Category: ${currentProduct.category ?: "Uncategorized"}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Stock: ${currentProduct.stock} units",
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    if (currentProduct.stock <= currentProduct.reorderLevel) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Low stock! Reorder soon.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Reorder Level: ${currentProduct.reorderLevel} units",
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Sold: ${currentProduct.sold} units",
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    if (currentProduct.barcode != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Barcode: ${currentProduct.barcode}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Description",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = currentProduct.description ?: "No description available",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showStockDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showStockDialog = false
                        newStockValue = ""
                        stockError = null
                    },
                    title = { Text("Update Stock") },
                    text = {
                        Column {
                            Text("Enter new stock quantity:")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newStockValue,
                                onValueChange = {
                                    newStockValue = it
                                    stockError = null
                                },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                isError = stockError != null,
                                supportingText = {
                                    stockError?.let { Text(it) }
                                },
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                try {
                                    val stockValue = newStockValue.toInt()
                                    if (stockValue < 0) {
                                        stockError = "Stock cannot be negative"
                                    } else {
                                        viewModel.updateStock(stockValue) {
                                            // On success
                                            showStockDialog = false
                                            newStockValue = ""
                                            stockError = null
                                        }
                                    }
                                } catch (e: NumberFormatException) {
                                    stockError = "Please enter a valid number"
                                }
                            }
                        ) {
                            Text("Update")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showStockDialog = false
                                newStockValue = ""
                                stockError = null
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
