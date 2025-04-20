package edu.cit.sarismart.features.user.tabs.sasa.data.repository

import android.util.Log
import edu.cit.sarismart.BuildConfig
import edu.cit.sarismart.core.util.Constants
import edu.cit.sarismart.features.user.tabs.sasa.data.models.ChatRequest
import edu.cit.sarismart.features.user.tabs.sasa.data.models.Content
import edu.cit.sarismart.features.user.tabs.sasa.data.models.Part
import edu.cit.sarismart.features.user.tabs.sasa.domain.ChatApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val chatApiService: ChatApiService): ChatRepository {

    override suspend fun sendMessage(message: String): String {
        return withContext(Dispatchers.IO) {
            try {

                val part = Part(message)
                val content = Content(parts = listOf(part))
                val req = ChatRequest(contents = listOf(content))

                val res = chatApiService.chat(Constants.GEMINI_MODEL, req, BuildConfig.GEMINI_API_KEY)

                if(res.isSuccessful) {
                    val candidate = res.body()?.candidates?.first()
                    val content = candidate?.content
                    val parts = content?.parts
                    val message = parts?.first()?.text

                    "$message"
                } else {
                    "Error: ${res.code()}"
                }
            } catch (e: Exception) {
                Log.d("ChatRepositoryImpl", e.message.toString())
                "Error: Timed out."
            }
        }
    }
}