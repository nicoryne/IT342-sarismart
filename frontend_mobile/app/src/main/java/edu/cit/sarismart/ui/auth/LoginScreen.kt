package edu.cit.sarismart.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import edu.cit.sarismart.R
import edu.cit.sarismart.util.BiometricUtil

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onCreateAccountClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onNavigateToGuest: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isBiometricAvailable by viewModel.isBiometricAvailable.collectAsState()

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                LoginNavigationEvent.NavigateToHome -> onNavigateToHome()
                LoginNavigationEvent.NavigateToGuestMap -> onNavigateToGuest()
                LoginNavigationEvent.NavigateToForgotPassword -> onForgotPasswordClick()
                LoginNavigationEvent.ShowBiometricPrompt -> {
                    activity?.let {
                        BiometricUtil.showBiometricPrompt(
                            activity = it,
                            title = "Login to SariSmart",
                            subtitle = "Use your fingerprint or face to log in",
                            onSuccess = { viewModel.onBiometricAuthSuccess() },
                            onError = { _, _ -> /* handle error */ },
                            onFailed = { /* handle failure */ }
                        )
                    }
                }
            }
        }
    }

    Box (
        modifier = Modifier.fillMaxSize()
    ) {
        // background pattern
        BackgroundPattern(
            primaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            secondaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )

        // actual screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))


            // login Title
            Text(
                text = "Login to SariSmart",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome back, Kamusta?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(32.dp))

            // email field
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // password field
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),

                ),
                trailingIcon = {
                    val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(icon, contentDescription = "Toggle password visibility")
                    }
                },
                singleLine = true
            )

            // forgot password
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Forgot Password?",
                    modifier = Modifier
                        .clickable(onClick = onForgotPasswordClick)
                        .padding(4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // login button
            Button(
                onClick = { viewModel.onLoginClicked() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isBiometricAvailable) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { viewModel.onBiometricLoginClicked() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fingerprint),
                        contentDescription = "Fingerprint",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Login with Biometrics",
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // or continue with
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                Text(
                    text = "or continue with",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // social login button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // google sign in
                ElevatedButton(
                    onClick = { viewModel.onLoginWithGoogleClicked() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google")
                }

                // facebook sign in
                ElevatedButton(
                    onClick = { viewModel.onLoginWithFacebookClicked() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Facebook")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // continue as guest
            OutlinedButton(
                onClick = { viewModel.onContinueAsGuestClicked(); onNavigateToGuest() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Continue as Guest",
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // create account
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Create Account",
                    modifier = Modifier.clickable(onClick = onCreateAccountClick),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }


}

@Composable
fun BackgroundPattern(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // top right curved shape
            val path1 = Path().apply {
                moveTo(width * 0.7f, 0f)
                cubicTo(
                    width * 0.8f, height * 0.1f,
                    width * 1.1f, height * 0.05f,
                    width, height * 0.3f
                )
                lineTo(width, 0f)
                close()
            }
            drawPath(path1, primaryColor, style = Fill)

            // bottom left blob
            val path2 = Path().apply {
                moveTo(0f, height * 0.7f)
                cubicTo(
                    width * 0.3f, height * 0.9f,
                    width * 0.6f, height * 1.1f,
                    width, height
                )
                lineTo(0f, height)
                close()
            }
            drawPath(path2, primaryColor, style = Fill)

            // middle right accent
            val path3 = Path().apply {
                moveTo(width * 0.85f, height * 0.45f)
                cubicTo(
                    width * 0.9f, height * 0.4f,
                    width * 1.1f, height * 0.5f,
                    width, height * 0.55f
                )
                lineTo(width, height * 0.45f)
                close()
            }
            drawPath(path3, secondaryColor, style = Fill)

            // draw scattered circles
            val circlePositions = listOf(
                // top-left
                Offset(width * 0.1f, height * 0.1f) to 40f,
                // center
                Offset(width * 0.8f, height * 0.7f) to 60f,
                // bottom-right
                Offset(width * 0.3f, height * 0.5f) to 30f,
                // off-center
                Offset(width * 0.6f, height * 0.4f) to 25f,
                // low left
                Offset(width * 0.2f, height * 0.8f) to 45f
            )

            circlePositions.forEach { (position, radius) ->
                drawCircle(
                    color = secondaryColor,
                    radius = radius,
                    center = position
                )
            }

            // draw small dots pattern
            for (i in 0 until 40) {
                val x = (width * (0.1f + 0.8f * (i % 8) / 8f))
                val y = (height * (0.1f + 0.8f * (i / 8) / 5f))
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.3f),
                    radius = 3f,
                    center = Offset(x, y)
                )
            }
        }
    }
}
