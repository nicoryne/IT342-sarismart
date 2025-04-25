package edu.cit.sarismart.features.auth.data.repository

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import edu.cit.sarismart.core.data.PreferencesManager
import edu.cit.sarismart.core.data.AccessTokenManager
import edu.cit.sarismart.core.data.RefreshTokenManager
import edu.cit.sarismart.core.data.UserDetailsManager
import edu.cit.sarismart.core.data.UserStoresManager
import edu.cit.sarismart.features.auth.data.models.AuthRequest
import edu.cit.sarismart.features.auth.data.models.AuthResponse
import edu.cit.sarismart.features.auth.data.models.ClientResponse
import edu.cit.sarismart.features.auth.domain.AuthApiService
import edu.cit.sarismart.features.user.tabs.account.data.models.User
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val accessTokenManager: AccessTokenManager,
    private val refreshTokenManager: RefreshTokenManager,
    private val preferencesManager: PreferencesManager,
    private val userDetailsManager: UserDetailsManager,
   ) : AuthRepository {

    override suspend fun login(email: String, password: String): ClientResponse {
        val authRequest = AuthRequest(email, password)
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<AuthResponse> = authApiService.login(authRequest)

                if (response.isSuccessful) {
                    // logout just incase
                    logout()

                    val body = response.body()
                    val accessToken = body?.accessToken
                    val refreshToken = body?.refreshToken
                    val expiresAt = body?.expiresAt
                    val user = body?.user

                    saveUser(user!!, accessToken!!, refreshToken!!, expiresAt!!)
                    ClientResponse(true, "Login Successful.")
                } else if (response.code() == 403) {
                    ClientResponse(false, "Please try again. Invalid login credentials.")
                } else {
                    ClientResponse(false, "Please try again. Account might not exist.")
                }
            } catch (e: Exception) {
                Log.e("AuthRepositoryImpl", "Error: ${e.message}")
                ClientResponse(false, "Please try again. Server might be down.")
            }
        }
    }

    override suspend fun loginWithBiometric(): ClientResponse {
        if(!accessTokenManager.isExpired()) {
            return ClientResponse(true, "Login Successful.")
        }

        return withContext(Dispatchers.IO) {
            try {
                val refreshToken = refreshTokenManager.getToken.first().toString()
                val response = authApiService.refresh(mapOf("refresh_token" to refreshToken))

                if (response.isSuccessful) {
                    logout()
                    val body = response.body()
                    val accessToken = body?.accessToken
                    val refreshToken = body?.refreshToken
                    val expiresAt = body?.expiresAt
                    val user = body?.user

                    saveUser(user!!, accessToken!!, refreshToken!!, expiresAt!!)
                    ClientResponse(true, "Login Successful.")
                } else if (response.code() == 403) {
                    ClientResponse(false, "Please try again. Invalid login credentials.")
                } else {
                    ClientResponse(false, "Please try again. Account might not exist.")
                }
            } catch (e: Exception) {
                Log.e("AuthRepositoryImpl", "Error: ${e.message}")
                ClientResponse(false, "Please try again. Server might be down.")
            }
        }


    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        verifyPassword: String
    ): ClientResponse {

        if(password != verifyPassword) {
            return ClientResponse(false, "Registration Failed. Passwords do not match.")
        }

        val authRequest = AuthRequest(email, password)

        return withContext(Dispatchers.IO) {
            try {
                val response: Response<String> = authApiService.register(authRequest)

                if (response.isSuccessful) {
                    ClientResponse(true, "Registration Successful.")
                } else {
                    ClientResponse(false, "Please try again. Server timed out.")
                }
            } catch (e: Exception) {
                ClientResponse(true, e.message.toString())
            }
        }
    }

    override suspend fun resetPassword(email: String): ClientResponse {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): ClientResponse {
        val token = runBlocking { accessTokenManager.getToken.first() }

        if (token.isNullOrEmpty()) {
            return ClientResponse(false, "User session not found.")
        }

        // clearing preferences
        preferencesManager.clear()

        // clearing token
        accessTokenManager.deleteToken()
        refreshTokenManager.deleteToken()

        // clearing user details
        userDetailsManager.clear()

        return ClientResponse(true, "Logout success.")
    }

    override fun isBiometricEnabled(): Boolean {
        val token = runBlocking { refreshTokenManager.getToken.first() }
        return !token.isNullOrEmpty()
    }

    override suspend fun saveUser(user: User, accessToken: String, refreshToken: String, expiresAt: Long) {
        accessTokenManager.saveToken(accessToken)
        accessTokenManager.setExpiresAt(expiresAt)
        refreshTokenManager.saveToken(refreshToken)
        userDetailsManager.saveUserDetails(user)
    }
}