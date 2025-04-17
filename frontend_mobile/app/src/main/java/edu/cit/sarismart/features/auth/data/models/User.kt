package edu.cit.sarismart.features.auth.data.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val aud: String,
    val role: String,
    val email: String,
    @SerializedName("email_confirmed_at") val emailConfirmedAt: String?,
    val phone: String,
)