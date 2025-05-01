package edu.cit.sarismart.features.user.tabs.stores.domain

import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.ProductRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface StoreProductApiService {

    @GET("/api/v1/stores/{storeId}/products")
    suspend fun listProducts(@Path("storeId") storeId: Long): Response<List<Product>>

    @POST("/api/v1/stores/{storeId}/products")
    suspend fun createProduct(@Path("storeId") storeId: Long, @Body product: ProductRequest): Response<Product>

    @PUT("/api/v1/stores/{storeId}/products/{productId}")
    suspend fun modifyProduct(@Path("storeId") storeId: Long, @Path("productId") productId: Long, @Body product: Product): Response<Product>

    @DELETE("/api/v1/stores/{storeId}/products/{productId}")
    suspend fun deleteProduct(@Path("storeId") storeId: Long, @Path("productId") productId: Long): Response<Void>

    @PATCH("/api/v1/stores/{storeId}/products/{productId}/stock")
    suspend fun adjustStock(@Path("storeId") storeId: Long, @Path("productId") productId: Long, @Query("quantity") quantity: Int): Response<Void>

}