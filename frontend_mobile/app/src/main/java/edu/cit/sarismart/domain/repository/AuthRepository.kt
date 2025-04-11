package edu.cit.sarismart.domain.repository

interface AuthRepository {
    fun login(email: String, password: String): Boolean

    fun loginWithBiometric(): Boolean

    fun register(name: String, email: String, password: String, verifyPassword: String): Boolean

    fun resetPassword(email: String): Boolean

    fun logout(): Boolean

    fun isBiometricEnabled(): Boolean

    fun setBiometricEnabled(enabled: Boolean)
}