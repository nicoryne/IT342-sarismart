package edu.cit.sarismart.features.user.tabs.stores.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.data.UserDetailsManager
import edu.cit.sarismart.core.data.UserStoresManager
import edu.cit.sarismart.core.network.BackendRetrofitClient
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepositoryImpl
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepositoryImpl
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreApiService
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreInventoryApiService
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreProductApiService
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreReportsApiService
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreTransactionsApiService
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreWorkerApiService
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StoreModule {

    @Provides
    @Singleton
    fun provideStoreApiService(@BackendRetrofitClient backendRetrofit: Retrofit): StoreApiService {
        return backendRetrofit.create(StoreApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStoreProductApiService(@BackendRetrofitClient backendRetrofit: Retrofit): StoreProductApiService {
        return backendRetrofit.create(StoreProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStoreInventoryApiService(@BackendRetrofitClient backendRetrofit: Retrofit): StoreInventoryApiService {
        return backendRetrofit.create(StoreInventoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStoreReportsApiService(@BackendRetrofitClient backendRetrofit: Retrofit): StoreReportsApiService {
        return backendRetrofit.create(StoreReportsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStoreTransactionsApiService(@BackendRetrofitClient backendRetrofit: Retrofit): StoreTransactionsApiService {
        return backendRetrofit.create(StoreTransactionsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStoreWorkerApiService(@BackendRetrofitClient backendRetrofit: Retrofit): StoreWorkerApiService {
        return backendRetrofit.create(StoreWorkerApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        storeProductApiService: StoreProductApiService,
        storeTransactionsApiService: StoreTransactionsApiService,
        storeInventoryApiService: StoreInventoryApiService,
        storeRepository: StoreRepository
    ): ProductRepository {
        return ProductRepositoryImpl(
            storeProductApiService,
            storeTransactionsApiService,
            storeInventoryApiService,
            storeRepository
        )
    }

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