package edu.cit.sarismart.features.user.tabs.stores.data.repository

import android.util.Log
import edu.cit.sarismart.core.data.UserDetailsManager
import edu.cit.sarismart.core.data.UserStoresManager
import edu.cit.sarismart.features.user.tabs.stores.data.exceptions.CreateStoreException
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import edu.cit.sarismart.features.user.tabs.stores.data.models.StoreRequest
import edu.cit.sarismart.features.user.tabs.stores.domain.StoreApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val storeApiService: StoreApiService,
    private val userDetailsManager: UserDetailsManager,
    private val userStoresManager: UserStoresManager
) : StoreRepository {

    override suspend fun createStore(
        storeName: String,
        storeLocation: String,
        storeLatitude: Double,
        storeLongitude: Double
    ): Result<Store> {
        return try {
            val ownerId = userDetailsManager.getUserId.first()

            if (ownerId == null) {
                Log.e("StoreRepository", "Owner ID is null")
                return Result.failure(CreateStoreException.MissingOwnerId)
            }

            if (storeName.isBlank() || storeLocation.isBlank() || storeLongitude.isNaN() || storeLatitude.isNaN()) {
                Log.e("StoreRepository", "Invalid store data")
                return Result.failure(CreateStoreException.InvalidInput("Store name, location, latitude, and longitude must be valid."))
            }

            val newStore = StoreRequest(
                storeName = storeName,
                location = storeLocation,
                latitude = storeLatitude,
                longitude = storeLongitude,
                ownerId = ownerId.toString()
            )

            val response = storeApiService.createStore(newStore)

            if (response.isSuccessful) {
                response.body()?.let {
                    userStoresManager.addToUserOwnerStores(it)
                    Result.success(it)
                } ?: Result.failure(CreateStoreException.EmptyResponseBody)
            } else {
                Log.e("StoreRepository", "API Error: ${response.code()} - ${response.message()}")
                Result.failure(CreateStoreException.ApiException(response.code(), response.message()))
            }
        } catch (e: Exception) {
            Log.e("StoreRepository", "An unexpected error occurred: ${e.localizedMessage ?: e.message ?: "Unknown error"}")
            Result.failure(CreateStoreException.UnknownError(e))
        }
    }

    override suspend fun updateStores() {
        val ownerId = userDetailsManager.getUserId.first()

        if (ownerId == null) {
            Log.e("StoreRepository", "Owner ID is null")
            return
        }

        try {
            val response = storeApiService.getStoresByOwnerId(ownerId)
            Log.i("StoreRepository", "Response: $response")

            if(response.isSuccessful) {
                response.body()?.let {
                    userStoresManager.saveUserOwnerStores(it)
                    Log.i("StoreRepository", "Stores updated successfully ${it}")
                }
            } else {
                Log.e("StoreRepository", "API Error: ${response.code()} - ${response.message()}")
            }

        } catch (e: Exception) {
            Log.e("StoreRepository", "An unexpected error occurred: ${e.localizedMessage ?: e.message ?: "Unknown error"}")
        }
    }

    override suspend fun getOwnedStores(): List<Store> {
        return userStoresManager.getUserOwnerStores().first()
    }

    override suspend fun getStoreById(id: Long): Store {
        return userStoresManager.getUserOwnerStores().first().find { it.id == id } ?: throw Exception("Store not found")
    }

    override suspend fun deleteStore(storeId: Long): Boolean {
        try {
            val response = storeApiService.deleteStore(storeId)

            if(response.isSuccessful) {
                return true
            } else {
                Log.e("StoreRepository", "API Error: ${response.code()} - ${response.message()}")
            }

        } catch (e: Exception) {
            Log.e("StoreRepository", "An unexpected error occurred: ${e.localizedMessage ?: e.message ?: "Unknown error"}")
        }

        return false
    }

    override suspend fun editStore(
        store: Store,
        storeName: String,
        storeLocation: String,
        storeLatitude: Double,
        storeLongitude: Double
    ) {

        try {
            val updateRequest = Store(
                storeName = storeName,
                location = storeLocation,
                latitude = storeLatitude,
                longitude = storeLongitude,
                id = store.id,
                owner = store.owner,
                workers = store.workers,
                sales = store.sales ?: emptyList(),
            )

            val response = storeApiService.updateStore(store.id, updateRequest)

            if (response.isSuccessful) {
                response.body()?.let { updatedStore ->
                    val currentStores = userStoresManager.getUserOwnerStores().first()
                    val updatedStores = currentStores.map {
                        if (it.id == updatedStore.id) updatedStore else it
                    }
                    userStoresManager.saveUserOwnerStores(updatedStores)
                }
            } else {
                Log.e("StoreRepository", "API Error: ${response.code()} - ${response.message()}")
                throw Exception("Failed to update store: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("StoreRepository", "An unexpected error occurred: ${e.localizedMessage ?: e.message ?: "Unknown error"}")
            throw e
        }
    }
}