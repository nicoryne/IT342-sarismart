package edu.cit.sarismart.features.user.tabs.stores.data.repository

import edu.cit.sarismart.features.user.tabs.scan.data.models.CartItem
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.ProductRequest
import edu.cit.sarismart.features.user.tabs.stores.data.models.Sale

interface ProductRepository {
    suspend fun getProductsForStore(storeId: Long): List<Product>
    suspend fun getProductById(productId: Long, storeId: Long): Product?
    suspend fun getProductByBarcode(barcode: String, storeId: Long): Product?
    suspend fun updateProductStock(productId: Long, storeId: Long, newStock: Int): Boolean
    suspend fun createSale(storeId: Long, totalAmount: Double, items: List<CartItem>): Sale?
    suspend fun getSalesForStore(storeId: Long): List<Sale>
    suspend fun getLowStockAlerts(storeId: Long): List<Product>
    suspend fun setReorderLevel(storeId: Long, productId: Long, level: Int): Boolean

    // New methods
    suspend fun createProduct(storeId: Long, product: ProductRequest): Product?
    suspend fun updateProduct(storeId: Long, productId: Long, product: Product): Product?
    suspend fun deleteProduct(storeId: Long, productId: Long): Boolean
}