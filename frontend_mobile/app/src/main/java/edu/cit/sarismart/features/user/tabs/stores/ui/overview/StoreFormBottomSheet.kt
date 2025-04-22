package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFormBottomSheet(
 viewModel: StoreOverviewScreenViewModel = hiltViewModel<StoreOverviewScreenViewModel>(),
 onDismissRequest: () -> Unit,
 onSelectLocation: () -> Unit,
 onSubmitLoading: () -> Unit,
 onSubmitSuccess: () -> Unit,
 onSubmitError: (it: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    val storeName by viewModel.storeName.collectAsState()
    val storeLocation by viewModel.storeLocation.collectAsState()
    val storeNameError by viewModel.storeNameError.collectAsState()
    val storeLocationError by viewModel.storeLocationError.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register your store with SariSmart!",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = storeName,
                onValueChange = { viewModel.updateStoreName(it) },
                label = { Text("Store Name *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = storeNameError != null,
                supportingText = {
                    storeNameError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = storeLocation,
                onValueChange = { viewModel.updateStoreLocation(it) },
                label = { Text("Store Location *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    ),
                trailingIcon = {
                    IconButton(onClick = onSelectLocation) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = "Open Map Selection",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                },
                singleLine = true,
                isError = storeLocationError != null,
                supportingText = {
                    storeLocationError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.onSubmit(
                            onSubmitting = onSubmitLoading,
                            onSubmitSuccess = onSubmitSuccess,
                            onSubmitError = { onSubmitError(it) },
                            onDismissRequest = { onDismissRequest }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Submit",
                    fontWeight = FontWeight.Medium,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    viewModel.onCancel(); onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Cancel",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}