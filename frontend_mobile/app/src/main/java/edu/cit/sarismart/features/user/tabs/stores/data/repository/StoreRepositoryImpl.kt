package edu.cit.sarismart.features.user.tabs.stores.data.repository

import android.util.Log
import edu.cit.sarismart.core.data.UserDetailsManager
import edu.cit.sarismart.features.user.tabs.account.data.repository.AccountRepository
import edu.cit.sarismart.features.user.tabs.account.domain.AccountService
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import edu.cit.sarismart.features.user.tabs.stores.data.models.StoreRequest
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(private val storeApiService: StoreApiService, private val userDetailsManager: UserDetailsManager): StoreRepository {

    override suspend fun createStore(
        storeName: String,
        storeLocation: String,
        storeLatitude: Double,
        storeLongitude: Double
    ) {
        val ownerId = userDetailsManager.getUserId.first().toString()
        val newStore = StoreRequest(storeName = storeName, location = storeLocation, latitude = storeLatitude, longitude = storeLongitude, ownerId = ownerId)

        Log.i("StoreRepository", "Creating store: $newStore")
        Log.i("StoreRepository", "Owner ID: $ownerId")
        val store = storeApiService.createStore(newStore)
        Log.i("StoreRepository", "Store created: ${store.body()}")
    }


}