package edu.cit.sarismart.features.user.tabs.account.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.features.user.tabs.account.data.repository.AccountRepository
import edu.cit.sarismart.features.user.tabs.account.data.repository.AccountRepositoryImpl
import edu.cit.sarismart.features.user.tabs.account.domain.AccountService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountModule {

    @Provides
    @Singleton
    fun provideAccountRepository(accountService: AccountService): AccountRepository {
        return AccountRepositoryImpl(accountService)
    }

}