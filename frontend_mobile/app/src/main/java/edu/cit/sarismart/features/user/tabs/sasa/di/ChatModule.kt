package edu.cit.sarismart.features.user.tabs.sasa.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.network.GeminiRetrofitClient
import edu.cit.sarismart.features.user.tabs.sasa.data.repository.ChatRepository
import edu.cit.sarismart.features.user.tabs.sasa.data.repository.ChatRepositoryImpl
import edu.cit.sarismart.features.user.tabs.sasa.domain.ChatApiService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatRepository(@GeminiRetrofitClient geminiRetrofitClient: Retrofit): ChatRepository {
        return ChatRepositoryImpl(
            geminiRetrofitClient.create<ChatApiService>(ChatApiService::class.java)
        )
    }
}