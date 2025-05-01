package edu.cit.sarismart.features.user.tabs.stores.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    storeId: Long,
    productId: Long,
    onNavigateBack: () -> Unit,
    viewModel: EditProductViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val productName by viewModel.productName.collectAsState()
    val productCategory by viewModel.productCategory.collectAsState()
    val productPrice by viewModel.productPrice.collectAsState()
    val productStock by viewModel.productStock.collectAsState()
    val productDescription by viewModel.productDescription.collectAsState()
    val productReorderLevel by viewModel.productReorderLevel.collectAsState()

    val nameError by viewModel.nameError.collectAsState()
    val priceError by viewModel.priceError.collectAsState()
    val stockError by viewModel.stockError.collectAsState()
    val reorderLevelError by viewModel.reorderLevelError.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
            if (isLoading && productName.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { viewModel.updateProductName(it) },
                        label = { Text("Product Name*") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError != null,
                        supportingText = {
                            nameError?.let { Text(it) }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = productCategory,
                        onValueChange = { viewModel.updateProductCategory(it) },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = productPrice,
                        onValueChange = { viewModel.updateProductPrice(it) },
                        label = { Text("Price*") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = priceError != null,
                        supportingText = {
                            priceError?.let { Text(it) }
                        },
                        singleLine = true,
                        prefix = { Text("$") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = productStock,
                        onValueChange = { viewModel.updateProductStock(it) },
                        label = { Text("Stock*") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = stockError != null,
                        supportingText = {
                            stockError?.let { Text(it) }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = productReorderLevel,
                        onValueChange = { viewModel.updateProductReorderLevel(it) },
                        label = { Text("Reorder Level") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = reorderLevelError != null,
                        supportingText = {
                            reorderLevelError?.let { Text(it) }
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = productDescription,
                        onValueChange = { viewModel.updateProductDescription(it) },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.updateProduct() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Update Product")
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    viewModel.resetState()
                },
                title = { Text("Success") },
                text = { Text("Product updated successfully!") },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            viewModel.resetState()
                            onNavigateBack()
                        }
                    ) {
                        Text("Back to Products")
                    }
                }
            )
        }
    }
}