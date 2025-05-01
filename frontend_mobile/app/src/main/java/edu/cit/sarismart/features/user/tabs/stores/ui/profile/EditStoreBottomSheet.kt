package edu.cit.sarismart.features.user.tabs.stores.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.text.font.FontWeight

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

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Store",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = storeName,
                onValueChange = { viewModel.updateStoreName(it) },
                label = { Text("Store Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = storeNameError != null,
                supportingText = {
                    storeNameError?.let { Text(it) }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = storeLocation,
                onValueChange = { viewModel.updateStoreLocation(it) },
                label = { Text("Store Location") },
                modifier = Modifier.fillMaxWidth(),
                isError = storeLocationError != null,
                supportingText = {
                    storeLocationError?.let { Text(it) }
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onSelectLocation) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Select Location"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}