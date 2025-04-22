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

            if(response.isSuccessful) {
                response.body()?.let {
                    userStoresManager.saveUserOwnerStores(it)
                }
            } else {
                Log.e("StoreRepository", "API Error: ${response.code()} - ${response.message()}")
            }

        } catch (e: Exception) {
            Log.e("StoreRepository", "An unexpected error occurred: ${e.localizedMessage ?: e.message ?: "Unknown error"}")
        }

    }
}

