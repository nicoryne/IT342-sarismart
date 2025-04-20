package edu.cit.sarismart.features.auth.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.data.PreferencesManager
import edu.cit.sarismart.core.data.AccessTokenManager
import edu.cit.sarismart.core.data.RefreshTokenManager
import edu.cit.sarismart.core.network.BackendRetrofitClient
import edu.cit.sarismart.features.auth.data.repository.AuthRepositoryImpl
import edu.cit.sarismart.features.auth.data.repository.AuthRepository
import edu.cit.sarismart.features.auth.domain.AuthApiService
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        @BackendRetrofitClient backendRetrofit: Retrofit,
        accessTokenManager: AccessTokenManager,
        preferencesManager: PreferencesManager,
        refreshTokenManager: RefreshTokenManager): AuthRepository {
        return AuthRepositoryImpl(
            backendRetrofit.create<AuthApiService>(AuthApiService::class.java),
            accessTokenManager,
            refreshTokenManager,
            preferencesManager
        )
    }
}