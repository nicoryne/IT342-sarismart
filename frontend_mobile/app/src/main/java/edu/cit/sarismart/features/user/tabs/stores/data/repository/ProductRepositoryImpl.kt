package edu.cit.sarismart.features.user.tabs.stores.data.repository

import edu.cit.sarismart.features.user.tabs.scan.data.models.CartItem
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.Sale
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreInventoryApiService
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreProductApiService
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreTransactionsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val storeProductApiService: StoreProductApiService,
    private val storeTransactionsApiService: StoreTransactionsApiService,
    private val storeInventoryApiService: StoreInventoryApiService,
    private val storeRepository: StoreRepository
) : ProductRepository {

    override suspend fun getProductsForStore(storeId: Long): List<Product> = withContext(Dispatchers.IO) {
        try {
            val response = storeProductApiService.listProducts(storeId)
            if (response.isSuccessful) {
                return@withContext response.body() ?: emptyList()
            }
            return@withContext emptyList()
        } catch (e: Exception) {
            // Log error
            return@withContext emptyList()
        }
    }

    override suspend fun getProductById(productId: Long, storeId: Long): Product? = withContext(Dispatchers.IO) {
        try {
            // In a real app, you would have a dedicated endpoint for this
            // For now, we'll get all products and filter
            val products = getProductsForStore(storeId)
            return@withContext products.find { it.id == productId }
        } catch (e: Exception) {
            // Log error
            return@withContext null
        }
    }

    override suspend fun getProductByBarcode(barcode: String, storeId: Long): Product? = withContext(Dispatchers.IO) {
        try {
            // In a real app, you would have a dedicated endpoint for barcode lookup
            // For now, we'll simulate it by returning a random product
            val products = getProductsForStore(storeId)
            if (products.isNotEmpty()) {
                // In a real app, you would search for the product with matching barcode
                // For demo purposes, we'll just return a random product
                return@withContext products.random()
            }
            return@withContext null
        } catch (e: Exception) {
            // Log error
            return@withContext null
        }
    }

    override suspend fun updateProductStock(productId: Long, storeId: Long, newStock: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            // Calculate the adjustment needed (negative for decrease, positive for increase)
            val product = getProductById(productId, storeId) ?: return@withContext false
            val adjustment = newStock - product.stock

            val response = storeProductApiService.adjustStock(storeId, productId, adjustment)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            // Log error
            return@withContext false
        }
    }

    override suspend fun createSale(storeId: Long, totalAmount: Double, items: List<CartItem>): Sale? = withContext(Dispatchers.IO) {
        try {
            // Create a Sale object from cart items
            val store = storeRepository.getStoreById(storeId)
            val sale = Sale(
                id = 0, // The server will assign an ID
                store = store,
                totalAmount = totalAmount,
                saleDate = LocalDateTime.now()
            )

            val response = storeTransactionsApiService.createSale(storeId, sale)
            if (response.isSuccessful) {
                return@withContext response.body()
            }
            return@withContext null
        } catch (e: Exception) {
            // Log error
            return@withContext null
        }
    }

    override suspend fun getSalesForStore(storeId: Long): List<Sale> = withContext(Dispatchers.IO) {
        try {
            val response = storeTransactionsApiService.listSales(storeId)
            if (response.isSuccessful) {
                return@withContext response.body() ?: emptyList()
            }
            return@withContext emptyList()
        } catch (e: Exception) {
            // Log error
            return@withContext emptyList()
        }
    }

    override suspend fun getLowStockAlerts(storeId: Long): List<Product> = withContext(Dispatchers.IO) {
        try {
            val response = storeInventoryApiService.restockAlert(storeId)
            if (response.isSuccessful) {
                return@withContext response.body() ?: emptyList()
            }
            return@withContext emptyList()
        } catch (e: Exception) {
            // Log error
            return@withContext emptyList()
        }
    }

    override suspend fun setReorderLevel(storeId: Long, productId: Long, level: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = storeInventoryApiService.setReorderLevel(storeId, productId, level)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            // Log error
            return@withContext false
        }
    }
}