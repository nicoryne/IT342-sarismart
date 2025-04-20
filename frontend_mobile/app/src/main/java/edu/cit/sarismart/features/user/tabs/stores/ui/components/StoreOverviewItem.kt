package edu.cit.sarismart.features.user.tabs.stores.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.cit.sarismart.R
import edu.cit.sarismart.features.user.tabs.stores.ui.util.StoreStatus

@Composable
fun StoreOverviewItem(
    onStoreItemClick: () -> Unit,
    storeName: String,
    storeLocation: String,
    isOwner: Boolean,
    status: StoreStatus
) {
    OutlinedButton(
        onClick = onStoreItemClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.dp, color = status.color)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tab_store),
                    contentDescription = "Store Icon",
                    tint = status.color,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column() {
                    Text(
                        text = storeName,
                        color = status.color,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = storeLocation,
                        color = Color(0xFF8B8A8A),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    val userTypeText = if (isOwner) "Owner" else "Worker"
                    Text(
                        text = userTypeText,
                        color = status.color,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(status.icon),
                    contentDescription = "Store Icon",
                    tint = status.color,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = status.label,
                    color = Color(0xFF8B8A8A),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
