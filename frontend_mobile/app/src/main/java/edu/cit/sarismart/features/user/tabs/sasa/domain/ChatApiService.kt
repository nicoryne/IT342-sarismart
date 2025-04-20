package edu.cit.sarismart.features.user.tabs.sasa.domain

import edu.cit.sarismart.features.user.tabs.sasa.data.models.ChatRequest
import edu.cit.sarismart.features.user.tabs.sasa.data.models.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {

    @POST("v1beta/models/{model}:generateContent")
    suspend fun chat(@Path("model") model: String, @Body chatRequest: ChatRequest, @Query("key") key: String): Response<ChatResponse>

}