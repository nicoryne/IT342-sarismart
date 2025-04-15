package edu.cit.sarismart.features.auth.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.data.PreferencesManager
import edu.cit.sarismart.core.data.TokenManager
import edu.cit.sarismart.features.auth.data.repository.AuthRepositoryImpl
import edu.cit.sarismart.features.auth.data.repository.AuthRepository
import edu.cit.sarismart.features.auth.domain.AuthApiService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(retrofit: Retrofit, tokenManager: TokenManager, preferencesManager: PreferencesManager): AuthRepository {
        return AuthRepositoryImpl(retrofit.create<AuthApiService>(AuthApiService::class.java), tokenManager, preferencesManager)
    }
}