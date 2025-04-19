package edu.cit.sarismart.features.auth.domain

import edu.cit.sarismart.features.auth.data.models.AuthRequest
import edu.cit.sarismart.features.auth.data.models.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/auth/register")
    suspend fun register(@Body authRequest: AuthRequest): Response<String>

    @POST("api/v1/auth/login")
    suspend fun login(@Body authRequest: AuthRequest): Response<AuthResponse>

}
