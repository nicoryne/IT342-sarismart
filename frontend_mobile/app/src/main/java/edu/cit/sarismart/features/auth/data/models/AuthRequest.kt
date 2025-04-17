package edu.cit.sarismart.features.auth.data.models

data class AuthRequest (
    val email: String,
    val password: String,
    val fullName: String = ""
)