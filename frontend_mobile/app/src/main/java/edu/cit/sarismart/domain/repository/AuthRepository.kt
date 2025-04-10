package edu.cit.sarismart.domain.repository

interface AuthRepository {
    fun login(email: String, password: String): Boolean

    fun register(name: String, email: String, password: String, verifyPassword: String): Boolean

    fun logout(): Boolean
}