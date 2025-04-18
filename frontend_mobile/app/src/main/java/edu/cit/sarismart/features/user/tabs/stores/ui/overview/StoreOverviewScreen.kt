package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.sarismart.features.user.components.Header

@Composable
fun StoreOverviewScreen(
    onNavigateToNotifications: () -> Unit,
) {

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Header("My Stores", onNavigateToNotifications)

    }
}
