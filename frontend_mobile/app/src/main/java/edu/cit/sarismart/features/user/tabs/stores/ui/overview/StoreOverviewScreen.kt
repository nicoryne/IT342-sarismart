package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.cit.sarismart.features.user.components.Header
import edu.cit.sarismart.features.user.tabs.stores.ui.components.RegisterNewStoreButton
import edu.cit.sarismart.features.user.tabs.stores.ui.components.StoreInfoBox
import edu.cit.sarismart.features.user.tabs.stores.ui.components.StoreOverviewItem
import edu.cit.sarismart.features.user.tabs.stores.ui.util.StoreStatus

data class DummyStore (
    val storeName: String,
    val storeLocation: String,
    val isOwner: Boolean,
    val storeStatus: StoreStatus
)

@Composable
fun StoreOverviewScreen(
    onNavigateToNotifications: () -> Unit,
    viewModel: StoreOverviewScreenViewModel = hiltViewModel(),
    onSelectLocation: () -> Unit,
    showBottomSheet: State<Boolean>,
    onShowBottomSheetChanged: (Boolean) -> Unit
) {

    val dummyStores = listOf(
        DummyStore("Store A", "Location 1", true, StoreStatus.GOOD),
        DummyStore("Store B", "Location 2", false, StoreStatus.LOW_STOCK),
        DummyStore("Store C", "Location 3", true, StoreStatus.OUT_OF_STOCK),
        DummyStore("Store D", "Location 4", false, StoreStatus.GOOD),
        DummyStore("Store E", "Location 5", true, StoreStatus.LOW_STOCK),
        DummyStore("Store F", "Location 6", false, StoreStatus.OUT_OF_STOCK)
    )

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
            // Registered Stores
            StoreInfoBox(
                modifier = Modifier.weight(1f),
                title = "You currently have",
                number = "2",
                subtitle = "stores registered"
            )

            // Restocking Due in X Days
            StoreInfoBox(
                modifier = Modifier.weight(1f),
                title = "Restocking due in",
                number = "2 days",
                subtitle = "for Oval's Place"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            RegisterNewStoreButton(onRegisterStoreClick = { onShowBottomSheetChanged(true)} )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = {  },
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
            items(dummyStores) { store ->
                StoreOverviewItem(
                    onStoreItemClick = { /* Handle item click */ },
                    storeName = store.storeName,
                    storeLocation = store.storeLocation,
                    isOwner = store.isOwner,
                    status = store.storeStatus
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showBottomSheet.value) {
            StoreFormBottomSheet(
                onDismissRequest = { onShowBottomSheetChanged(false) },
                onSelectLocation = {
                    onSelectLocation(); onShowBottomSheetChanged(true) }
            )
        }
    }
}




