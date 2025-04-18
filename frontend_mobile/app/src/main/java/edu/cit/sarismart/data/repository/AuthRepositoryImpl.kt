package edu.cit.sarismart.data.repository

import edu.cit.sarismart.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {

    override fun login(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun loginWithBiometric(): Boolean {
        TODO("Not yet implemented")
    }

    override fun register(
        name: String,
        email: String,
        password: String,
        verifyPassword: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun resetPassword(email: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun logout(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isBiometricEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }
}