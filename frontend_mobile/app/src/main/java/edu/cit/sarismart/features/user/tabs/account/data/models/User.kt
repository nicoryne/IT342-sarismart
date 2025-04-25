package edu.cit.sarismart.features.user.tabs.account.data.models

data class User (
    val id: String,
    val aud: String?,
    val email: String,
    val phone: String?,
    val fullName: String?
)