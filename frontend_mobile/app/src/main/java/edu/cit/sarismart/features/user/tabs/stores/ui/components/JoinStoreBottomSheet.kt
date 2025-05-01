package edu.cit.sarismart.features.user.tabs.stores.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.cit.sarismart.features.user.tabs.stores.ui.overview.StoreOverviewScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinStoreBottomSheet(
    viewModel: StoreOverviewScreenViewModel,
    onDismissRequest: () -> Unit
) {
    val joinCode by viewModel.joinCode.collectAsState()
    val isJoining by viewModel.isJoining.collectAsState()
    val joinError by viewModel.joinError.collectAsState()
    val joinSuccess by viewModel.joinSuccess.collectAsState()

    ModalBottomSheet(
        onDismissRequest = { if (!isJoining) onDismissRequest() },
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
                    text = "Join Store as Worker",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { if (!isJoining) onDismissRequest() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Enter the invitation code provided by the store owner:",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = joinCode,
                onValueChange = { viewModel.updateJoinCode(it) },
                label = { Text("Invitation Code") },
                modifier = Modifier.fillMaxWidth(),
                isError = joinError != null,
                enabled = !isJoining,
                singleLine = true
            )

            if (joinError != null) {
                Text(
                    text = joinError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }

            if (joinSuccess != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = joinSuccess ?: "",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.joinStoreAsWorker() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isJoining && joinCode.isNotBlank()
            ) {
                if (isJoining) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Join Store")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
