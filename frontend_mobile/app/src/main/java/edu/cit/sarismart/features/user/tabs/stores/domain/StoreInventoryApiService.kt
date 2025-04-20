package edu.cit.sarismart.features.user.tabs.stores.domain

import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreInventoryApiService {

    @GET("/api/v1/{storeId}/inventory/alerts")
    suspend fun restockAlert(@Path("storeId") storeId: Long): Response<List<Product>>

    @PUT("/api/v1/{storeId}/inventory/{productId}/reorder")
    suspend fun setReorderLevel(@Path("storeId") storeId: Long, @Path("productId") productId: Long, @Query("level") level: Int): Response<Void>

}