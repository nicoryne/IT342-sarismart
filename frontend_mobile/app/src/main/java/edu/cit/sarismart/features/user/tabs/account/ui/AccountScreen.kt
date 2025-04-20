package edu.cit.sarismart.features.user.tabs.account.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.cit.sarismart.features.user.components.Header
import edu.cit.sarismart.features.user.tabs.account.ui.components.UserDetailBox

@Composable
fun AccountScreen (
    onNavigateToLogin: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel<AccountViewModel>()
) {
    LaunchedEffect(key1 = true) {
        viewModel.getUserDetails()
    }

    val userName = viewModel.userName.collectAsState()
    val userEmail = viewModel.userEmail.collectAsState()
    val userPhone = viewModel.userPhone.collectAsState()
    val nickname = if (userName.value.isEmpty()) {
        "User"
    } else if(userName.value.contains(" ")) {
        userName.value.split(" ")[0]
    } else {
        userName.value
    }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                AccountNavigationEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Header("My Account", onNavigateToNotifications)

            Spacer(modifier = Modifier.height(64.dp))

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Hello, ${nickname}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,

                )

                Spacer(modifier = Modifier.height(16.dp))

                UserDetailBox(
                    label = "Full Name",
                    value = userName.value,
                    isEditable = true,
                    onEditClick = { /* TODO */}
                )

                UserDetailBox(
                    label = "Email",
                    value = userEmail.value,
                    isEditable = false,
                    onEditClick = { /* TODO */}
                )

                UserDetailBox(
                    label = "Phone Number",
                    value = userPhone.value,
                    isEditable = true,
                    onEditClick = { /* TODO */}
                )
            }

            OutlinedButton(
                onClick = { viewModel.onLogoutClicked()},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Logout",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}