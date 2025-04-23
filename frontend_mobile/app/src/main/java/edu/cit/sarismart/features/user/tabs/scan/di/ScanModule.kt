package edu.cit.sarismart.features.user.tabs.scan.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.data.CartManager
import edu.cit.sarismart.features.user.tabs.scan.data.repository.CartRepository
import edu.cit.sarismart.features.user.tabs.scan.data.repository.CartRepositoryImpl
import edu.cit.sarismart.features.user.tabs.stores.data.repository.ProductRepository
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
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
}