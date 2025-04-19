package edu.cit.sarismart.features.user.tabs.sasa.data.repository


interface ChatRepository {

    suspend fun sendMessage(message: String): String

}