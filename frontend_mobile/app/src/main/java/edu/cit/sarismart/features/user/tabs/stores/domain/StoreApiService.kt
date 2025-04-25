package edu.cit.sarismart.features.user.tabs.stores.domain

import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import edu.cit.sarismart.features.user.tabs.stores.data.models.StoreRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface StoreApiService {

    @POST("/api/v1/stores")
    suspend fun createStore(@Body storeRequest: StoreRequest): Response<Store>

    @GET("/api/v1/stores/{storeId}")
    suspend fun getStore(@Path("storeId") storeId: Long): Response<Store>

    @GET("/api/v1/stores/owner/{ownerId}")
    suspend fun getStoresByOwnerId(@Path("ownerId") ownerId: String): Response<List<Store>>

    @PUT("/api/v1/stores/{storeId}")
    suspend fun updateStore(@Path("storeId") storeId: Long, @Body store: Store): Response<Store>

    @DELETE("/api/v1/stores/{storeId}")
    suspend fun deleteStore(@Path("storeId") storeId: Long): Response<Void>

}