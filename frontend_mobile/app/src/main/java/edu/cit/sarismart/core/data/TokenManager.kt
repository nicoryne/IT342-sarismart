package edu.cit.sarismart.core.data

import kotlinx.coroutines.flow.Flow

interface TokenManager {

    val getToken: Flow<String?>

    suspend fun saveToken(token: String)

    suspend fun deleteToken()
}