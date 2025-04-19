package edu.cit.sarismart.features.user.tabs.sasa.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.features.auth.domain.AuthApiService
import edu.cit.sarismart.features.user.tabs.sasa.data.repository.ChatRepository
import edu.cit.sarismart.features.user.tabs.sasa.data.repository.ChatRepositoryImpl
import edu.cit.sarismart.features.user.tabs.sasa.domain.ChatService
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatRepository(@Named("GeminiRetrofit") geminiRetrofit: Retrofit): ChatRepository {
        return ChatRepositoryImpl(
            geminiRetrofit.create<ChatService>(ChatService::class.java)
        )
    }
}