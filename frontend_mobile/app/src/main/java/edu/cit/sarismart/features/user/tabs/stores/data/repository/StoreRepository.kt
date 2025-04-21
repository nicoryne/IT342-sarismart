package edu.cit.sarismart.features.user.tabs.stores.data.repository

interface StoreRepository {

    suspend fun createStore(storeName: String, storeLocation: String, storeLatitude: Double, storeLongitude: Double)


}