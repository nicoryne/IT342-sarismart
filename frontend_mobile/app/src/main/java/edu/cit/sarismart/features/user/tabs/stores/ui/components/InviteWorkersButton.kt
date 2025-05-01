package edu.cit.sarismart.features.user.tabs.stores.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun InviteWorkersButton(
    onInviteWorkersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onInviteWorkersClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = "Invite Workers"
        )
        Text(
            text = "Invite Workers",
            fontWeight = FontWeight.Medium
        )
    }
}
