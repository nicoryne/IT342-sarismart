package edu.cit.sarismart.features.user.tabs.account.domain

import edu.cit.sarismart.features.user.tabs.account.data.models.User
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Query

interface AccountService {

    @PUT
    suspend fun updateUserInfo(@Body details: User, @Query("token") token: String)

}