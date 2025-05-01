package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.cit.sarismart.features.user.components.Header
import edu.cit.sarismart.features.user.tabs.stores.ui.components.JoinStoreBottomSheet
import edu.cit.sarismart.features.user.tabs.stores.ui.components.RegisterNewStoreButton
import edu.cit.sarismart.features.user.tabs.stores.ui.components.StoreInfoBox
import edu.cit.sarismart.features.user.tabs.stores.ui.components.StoreOverviewItem
import edu.cit.sarismart.features.user.tabs.stores.ui.util.StoreStatus

@Composable
fun StoreOverviewScreen(
    onNavigateToNotifications: () -> Unit,
    viewModel: StoreOverviewScreenViewModel = hiltViewModel(),
    onSelectLocation: () -> Unit,
    onNavigateToProfile: (storeId: Long) -> Unit
) {
    val showBottomSheet by viewModel.showBottomSheet.collectAsState()
    val isSubmitLoading by viewModel.isSubmitLoading.collectAsState()
    val isSubmitError by viewModel.isSubmitError.collectAsState()
    val isSubmitSuccess by viewModel.isSubmitSuccess.collectAsState()
    val showSubmitDialog by viewModel.showSubmitDialog.collectAsState()
    val stores by viewModel.stores.collectAsState()
    val storeStatuses by viewModel.storeStatuses.collectAsState()
    val restockingStore by viewModel.restockingStore.collectAsState()
    val restockingDays by viewModel.restockingDays.collectAsState()
    val showJoinDialog by viewModel.showJoinDialog.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.getStores()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("My Stores", onNavigateToNotifications)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // registered stores
            StoreInfoBox(
                modifier = Modifier.weight(1f),
                title = "You have",
                number = stores.size.toString(),
                subtitle = "stores registered"
            )

            // restocking due in x days
            StoreInfoBox(
                modifier = Modifier.weight(1f),
                title = "Restocking due in",
                number = if (restockingDays > 0) "$restockingDays days" else "N/A",
                subtitle = restockingStore?.storeName ?: "No stores need restocking"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            RegisterNewStoreButton(onRegisterStoreClick = { viewModel.onShowBottomSheetChanged(true)} )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = { viewModel.showJoinDialog() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Join Store as a Worker",
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // store overview items here
        LazyColumn () {
            items(stores, key = { store -> store.id }) { store ->
                StoreOverviewItem(
                    onStoreItemClick = {
                        onNavigateToProfile(store.id)
                        Log.d("StoreOverviewScreen", "Navigating to store profile with ID: ${store.id}")
                    },
                    storeName = store.storeName,
                    storeLocation = store.location,
                    isOwner = true,
                    status = storeStatuses[store.id] ?: StoreStatus.GOOD
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showBottomSheet) {
            StoreFormBottomSheet(
                onDismissRequest = { viewModel.onShowBottomSheetChanged(false) },
                onSelectLocation = {
                    onSelectLocation(); viewModel.onShowBottomSheetChanged(true)
                },
                onSubmitLoading = { viewModel.onSubmitLoading() },
                onSubmitSuccess = { viewModel.onSubmitSuccess() },
                onSubmitError = { viewModel.onSubmitError(it) }
            )
        }

        if (showJoinDialog) {
            JoinStoreBottomSheet(
                viewModel = viewModel,
                onDismissRequest = { viewModel.dismissJoinDialog() }
            )
        }

        if (showSubmitDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissSubmitDialog() },
                title = {
                    Text(
                        text = if (isSubmitLoading) "Creating Store"
                        else if (isSubmitSuccess != null) "Success"
                        else if (isSubmitError != null) "Error"
                        else ""
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isSubmitLoading) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Creating your new store...")
                        } else {
                            isSubmitSuccess?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                            isSubmitError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    }
                },
                confirmButton = {
                    if (!isSubmitLoading) {
                        Button(onClick = { viewModel.onDismissSubmitDialog(); viewModel.onShowBottomSheetChanged(false) }) {
                            Text("Okay")
                        }
                    }
                },
                dismissButton = null
            )
        }
    }
}
