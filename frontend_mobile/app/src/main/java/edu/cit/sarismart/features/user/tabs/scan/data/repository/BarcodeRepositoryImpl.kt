package edu.cit.sarismart.features.user.tabs.scan.data.repository

import android.util.Log
import edu.cit.sarismart.BuildConfig
import edu.cit.sarismart.features.user.tabs.scan.data.models.BarcodeProductResponse
import edu.cit.sarismart.features.user.tabs.scan.domain.BarcodeApiService
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.ProductRequest
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeRepositoryImpl @Inject constructor(
    private val barcodeApiService: BarcodeApiService
) : BarcodeRepository {

    companion object {
        private const val UPC_DATABASE_API_KEY = ""
        private const val DEFAULT_PRODUCT_PRICE = 0.0
        private const val DEFAULT_PRODUCT_STOCK = 0
    }

    override suspend fun lookupBarcode(barcode: String): BarcodeProductResponse? = withContext(Dispatchers.IO) {
        try {
            // Try Open Food Facts API first
            val openFoodFactsResponse = tryOpenFoodFactsApi(barcode)
            if (openFoodFactsResponse != null) {
                return@withContext openFoodFactsResponse
            }

            // If not found, try UPC Database API
            val upcDatabaseResponse = tryUpcDatabaseApi(barcode)
            if (upcDatabaseResponse != null) {
                return@withContext upcDatabaseResponse
            }

            // If all APIs fail, return null
            return@withContext null
        } catch (e: Exception) {
            // Log error
            return@withContext null
        }
    }

    private suspend fun tryOpenFoodFactsApi(barcode: String): BarcodeProductResponse? {
        try {
            val response = barcodeApiService.getOpenFoodFactsProduct(barcode)
            if (response.isSuccessful) {
                val product = response.body()?.product
                if (product != null && !product.product_name.isNullOrBlank()) {
                    return BarcodeProductResponse(
                        name = product.product_name ?: "Unknown Product",
                        brand = product.brands,
                        description = null,
                        imageUrl = product.image_url,
                        price = null,
                        barcode = barcode
                    )
                }
            }
        } catch (e: Exception) {
            // Log error but continue to next API
        }
        return null
    }

    private suspend fun tryUpcDatabaseApi(barcode: String): BarcodeProductResponse? {
        try {
            val response = barcodeApiService.getUpcDatabaseProduct(barcode)
            if (response.isSuccessful && response.body()?.success == true) {
                val product = response.body()?.product
                if (product != null && !product.title.isNullOrBlank()) {
                    return BarcodeProductResponse(
                        name = product.title ?: "Unknown Product",
                        brand = product.brand,
                        description = product.description,
                        imageUrl = product.images?.firstOrNull(),
                        barcode = barcode,
                        price = product.highest_recorded_price
                    )
                }
            }
        } catch (e: Exception) {
            // Log error but continue
            Log.e("BarcodeRepository", "Error fetching product from UPC Database API", e)
        }
        return null
    }

    override suspend fun createProductFromBarcode(barcode: String, storeId: Long, store: Store): ProductRequest? {
        val productInfo = lookupBarcode(barcode) ?: return null

        // Create a new product with the information from the barcode lookup
        return ProductRequest(
            name = productInfo.name,
            description = productInfo.description ?: "Product added from barcode scan",
            price = productInfo.price ?: DEFAULT_PRODUCT_PRICE,
            stock = DEFAULT_PRODUCT_STOCK,
            barcode = barcode,
            category = productInfo.brand.toString(),
            reorder_level = 1,
        )
    }
}