package edu.cit.sarismart.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.domain.repository.AuthRepository
import edu.cit.sarismart.data.repository.AuthRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }
}