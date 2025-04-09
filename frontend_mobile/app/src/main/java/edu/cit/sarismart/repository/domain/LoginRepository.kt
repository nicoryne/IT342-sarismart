package edu.cit.sarismart.repository.domain

interface LoginRepository {
    fun login(email: String, password: String): Boolean
}