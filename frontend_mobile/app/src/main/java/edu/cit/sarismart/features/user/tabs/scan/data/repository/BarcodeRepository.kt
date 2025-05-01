package edu.cit.sarismart.features.user.tabs.scan.data.repository

import edu.cit.sarismart.features.user.tabs.scan.data.models.BarcodeProductResponse
import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.ProductRequest
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store


interface BarcodeRepository {
    suspend fun lookupBarcode(barcode: String): BarcodeProductResponse?
    suspend fun createProductFromBarcode(barcode: String, storeId: Long, store: Store): ProductRequest?
}

