package edu.cit.sarismart.features.auth.data.repository

interface AuthRepository {

    suspend fun login(email: String, password: String): Boolean

    suspend fun loginWithBiometric(): Boolean

    suspend fun register(name: String, email: String, password: String, verifyPassword: String): Boolean

    suspend fun resetPassword(email: String): Boolean

    suspend fun logout(): Boolean

    fun isBiometricEnabled(): Boolean

    fun setBiometricEnabled(enabled: Boolean)
}