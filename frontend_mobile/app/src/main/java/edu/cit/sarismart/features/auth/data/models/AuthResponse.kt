package edu.cit.sarismart.features.auth.data.models

import com.google.gson.annotations.SerializedName
import edu.cit.sarismart.features.user.tabs.account.data.models.User

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("expires_at") val expiresAt: Long,
    @SerializedName("refresh_token") val refreshToken: String,
    val user: User
)