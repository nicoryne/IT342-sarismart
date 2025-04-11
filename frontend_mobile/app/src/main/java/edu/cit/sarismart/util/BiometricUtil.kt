package edu.cit.sarismart.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BiometricUtil {
    companion object {
        /**
         * checks if the device supports biometric authentication and if biometrics are enrolled
         */
        fun canAuthenticate(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> true
                else -> false
            }
        }

        /**
         * shows the biometric prompt and returns the result via callback
         */
        fun showBiometricPrompt(
            activity: FragmentActivity,
            title: String = "Biometric Authentication",
            subtitle: String = "Log in using your biometric credential",
            negativeButtonText: String = "Cancel",
            onSuccess: () -> Unit,
            onError: (Int, String) -> Unit,
            onFailed: () -> Unit
        ) {
            val executor = ContextCompat.getMainExecutor(activity)

            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()

            BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
        }

        /**
         * shows the biometric prompt and returns the result via suspending function
         */
        suspend fun showBiometricPromptSuspend(
            activity: FragmentActivity,
            title: String = "Biometric Authentication",
            subtitle: String = "Log in using your biometric credential",
            negativeButtonText: String = "Cancel"
        ): BiometricResult = suspendCancellableCoroutine { continuation ->
            showBiometricPrompt(
                activity = activity,
                title = title,
                subtitle = subtitle,
                negativeButtonText = negativeButtonText,
                onSuccess = {
                    if (continuation.isActive) continuation.resume(BiometricResult.Success)
                },
                onError = { errorCode, errString ->
                    if (continuation.isActive) continuation.resume(BiometricResult.Error(errorCode, errString))
                },
                onFailed = {
                    if (continuation.isActive) continuation.resume(BiometricResult.Failed)
                }
            )
        }
    }

    sealed class BiometricResult {
        object Success : BiometricResult()
        data class Error(val errorCode: Int, val errorMessage: String) : BiometricResult()
        object Failed : BiometricResult()
    }
}
