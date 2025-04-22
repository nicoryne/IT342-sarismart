package edu.cit.sarismart.features.user.tabs.stores.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.data.UserDetailsManager
import edu.cit.sarismart.core.data.UserStoresManager
import edu.cit.sarismart.core.network.BackendRetrofitClient
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepositoryImpl
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreApiService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StoreModule {

    @Provides
    @Singleton
    fun provideStoreRepository(
        @BackendRetrofitClient backendRetrofit: Retrofit,
        userDetailsManager: UserDetailsManager,
        storesManager: UserStoresManager
    ): StoreRepository {
        return StoreRepositoryImpl(
            backendRetrofit.create<StoreApiService>(StoreApiService::class.java),
            userDetailsManager,
            storesManager
        )
    }


}