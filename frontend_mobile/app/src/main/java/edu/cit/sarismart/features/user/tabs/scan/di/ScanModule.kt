package edu.cit.sarismart.features.user.tabs.scan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.data.CartManager
import edu.cit.sarismart.core.network.BackendRetrofitClient
import edu.cit.sarismart.core.network.UpcRetrofitClient
import edu.cit.sarismart.core.util.Constants
import edu.cit.sarismart.features.user.tabs.scan.data.repository.BarcodeRepository
import edu.cit.sarismart.features.user.tabs.scan.data.repository.BarcodeRepositoryImpl
import edu.cit.sarismart.features.user.tabs.scan.data.repository.CartRepository
import edu.cit.sarismart.features.user.tabs.scan.data.repository.CartRepositoryImpl
import edu.cit.sarismart.features.user.tabs.scan.domain.BarcodeApiService
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScanModule {

    @Provides
    @Singleton
    fun provideCartManager(@ApplicationContext context: Context): CartManager {
        return CartManager(context)
    }

    @Provides
    @Singleton
    fun provideCartRepository(
        cartManager: CartManager,
        productRepository: ProductRepository,
        storeRepository: StoreRepository
    ): CartRepository {
        return CartRepositoryImpl(
            cartManager,
            productRepository,
            storeRepository
        )
    }

    @Provides
    @Singleton
    @Named("OpenFoodFactsRetrofit")
    fun provideOpenFoodFactsRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("UpcDatabaseRetrofit")
    fun provideUpcDatabaseRetrofit(
        @UpcRetrofitClient retrofit: Retrofit
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.UPC_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBarcodeApiService(
        @Named("OpenFoodFactsRetrofit") openFoodFactsRetrofit: Retrofit
    ): BarcodeApiService {
        return openFoodFactsRetrofit.create(BarcodeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBarcodeRepository(
        barcodeApiService: BarcodeApiService
    ): BarcodeRepository {
        return BarcodeRepositoryImpl(barcodeApiService)
    }
}