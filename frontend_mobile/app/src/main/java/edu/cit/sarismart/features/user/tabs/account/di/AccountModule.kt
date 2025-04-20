package edu.cit.sarismart.features.user.tabs.account.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.network.BackendRetrofitClient
import edu.cit.sarismart.features.user.tabs.account.data.repository.AccountRepository
import edu.cit.sarismart.features.user.tabs.account.data.repository.AccountRepositoryImpl
import edu.cit.sarismart.features.user.tabs.account.domain.AccountService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountModule {

    @Provides
    @Singleton
    fun provideAccountRepository(@BackendRetrofitClient backendRetrofit: Retrofit, accountService: AccountService): AccountRepository {
        return AccountRepositoryImpl(accountService)
    }

}