package edu.cit.sarismart.features.auth.data.repository

import edu.cit.sarismart.features.auth.data.models.ClientResponse

interface AuthRepository {

    suspend fun login(email: String, password: String): ClientResponse

    suspend fun loginWithBiometric(): ClientResponse

    suspend fun register(name: String, email: String, password: String, verifyPassword: String): ClientResponse

    suspend fun resetPassword(email: String): ClientResponse

    suspend fun logout(): ClientResponse

    fun isBiometricEnabled(): Boolean
}