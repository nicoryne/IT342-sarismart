package edu.cit.sarismart.features.auth.data.repository

import android.util.Log
import edu.cit.sarismart.core.data.PreferencesManager
import edu.cit.sarismart.core.data.TokenManager
import edu.cit.sarismart.features.auth.data.models.AuthRequest
import edu.cit.sarismart.features.auth.data.models.AuthResponse
import edu.cit.sarismart.features.auth.domain.AuthApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(private val authApiService: AuthApiService, private val tokenManager: TokenManager, private val preferencesManager: PreferencesManager) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean {
        val authRequest = AuthRequest(email, password)

        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = authApiService.login(authRequest)

                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.accessToken
                    Log.i("AUTHENTICATION", "Login Successful.")

                    if (token != null) {
                        tokenManager.saveToken(token)
                    }

                    true
                } else {
                    Log.e("AUTHENTICATION", "Login Failed. Code: ${response.code()} Message: ${response.message()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("AUTHENTICATION", "Login Error: ${e.message}")
                false
            }
        }
    }


    override suspend fun loginWithBiometric(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        verifyPassword: String
    ): Boolean {
        val authRequest = AuthRequest(email, password)

        if(password != verifyPassword) {
            Log.e("AUTHENTICATION", "Registration Failed. Passwords do not match.")
            return false;
        }

        return withContext(Dispatchers.IO) {
            try {
                val response: Response<String> = authApiService.register(authRequest)

                if (response.isSuccessful) {
                    val token = response.body()
                    Log.i("AUTHENTICATION", "Registration Successful")
                    true
                } else {
                    Log.e("AUTHENTICATION", "Registration Failed. Code: ${response.code()} Message: ${response.message()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("AUTHENTICATION", "Registration Error: ${e.message}")
                false
            }
        }
    }

    override suspend fun resetPassword(email: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Boolean {

        if (tokenManager.getToken.toString().isEmpty()) {
            return false
        }

        // clearing preferences
        preferencesManager.clear()

        // clearing token
        tokenManager.deleteToken()

        return true
    }

    override fun isBiometricEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }
}