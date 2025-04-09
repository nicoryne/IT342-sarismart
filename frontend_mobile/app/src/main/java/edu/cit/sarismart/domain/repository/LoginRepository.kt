package edu.cit.sarismart.domain.repository

interface LoginRepository {
    fun login(email: String, password: String): Boolean
}