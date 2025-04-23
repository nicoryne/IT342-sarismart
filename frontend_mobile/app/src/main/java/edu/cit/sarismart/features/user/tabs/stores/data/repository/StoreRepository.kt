package edu.cit.sarismart.features.user.tabs.stores.data.repository

import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import retrofit2.Response

interface StoreRepository {

    suspend fun createStore(storeName: String, storeLocation: String, storeLatitude: Double, storeLongitude: Double): Result<Store>

    suspend fun updateStores()

    suspend fun getOwnedStores(): List<Store>

    suspend fun getStoreById(id: Long): Store

}