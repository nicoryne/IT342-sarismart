package edu.cit.sarismart.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.repository.domain.LoginRepository
import edu.cit.sarismart.repository.data.LoginRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    fun provideLoginRepository(): LoginRepository {
        return LoginRepositoryImpl()
    }
}