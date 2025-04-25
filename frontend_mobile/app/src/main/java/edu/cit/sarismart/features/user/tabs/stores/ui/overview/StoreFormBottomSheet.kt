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
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFormBottomSheet(
    viewModel: StoreOverviewScreenViewModel = hiltViewModel<StoreOverviewScreenViewModel>(),
    onDismissRequest: () -> Unit,
    onSelectLocation: () -> Unit,
    onSubmitLoading: () -> Unit,
    onSubmitSuccess: () -> Unit,
    onSubmitError: (it: String) -> Unit,
    store: Store? = null,
    customStoreName: String? = null,
    customStoreLocation: String? = null,
    customStoreNameError: String? = null,
    customStoreLocationError: String? = null,
    onStoreNameChange: ((String) -> Unit)? = null,
    onStoreLocationChange: ((String) -> Unit)? = null,
    onCustomSubmit: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState()

    val storeName = customStoreName ?: viewModel.storeName.collectAsState().value
    val storeLocation = customStoreLocation ?: viewModel.storeLocation.collectAsState().value
    val storeNameError = customStoreNameError ?: viewModel.storeNameError.collectAsState().value
    val storeLocationError = customStoreLocationError ?: viewModel.storeLocationError.collectAsState().value

    val isEditMode = store != null

    LaunchedEffect(store) {
        if (store != null && onStoreNameChange == null) {
            viewModel.updateStoreName(store.storeName)
            viewModel.updateStoreLocation(store.location)
            viewModel.updateStoreLongitude(store.longitude)
            viewModel.updateStoreLatitude(store.latitude)
        }
    }

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
                text = if (isEditMode) "Edit your store details" else "Register your store with SariSmart!",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = storeName,
                onValueChange = {
                    if (onStoreNameChange != null) {
                        onStoreNameChange(it)
                    } else {
                        viewModel.updateStoreName(it)
                    }
                },
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
                onValueChange = {
                    if (onStoreLocationChange != null) {
                        onStoreLocationChange(it)
                    } else {
                        viewModel.updateStoreLocation(it)
                    }
                },
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
                    if (onCustomSubmit != null) {
                        onCustomSubmit()
                    } else {
                        viewModel.viewModelScope.launch {
                            viewModel.onSubmit(
                                onSubmitting = onSubmitLoading,
                                onSubmitSuccess = onSubmitSuccess,
                                onSubmitError = { onSubmitError(it) },
                                onDismissRequest = { onDismissRequest }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    if (isEditMode) "Save Changes" else "Submit",
                    fontWeight = FontWeight.Medium,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    if (onStoreNameChange != null) {
                        // We're in edit mode with custom handlers
                        onDismissRequest()
                    } else {
                        viewModel.onCancel(); onDismissRequest()
                    }
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