package edu.cit.sarismart.features.user.tabs.stores.domain

import edu.cit.sarismart.features.user.tabs.stores.data.models.Sale
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StoreTransactionsApiService {

    @POST("/api/v1/stores/{storeId}/transactions/sales")
    suspend fun createSale(@Path("storeId") storeId: Long, @Body sale: Sale): Response<Sale>

    @GET("/api/v1/stores/{storeId}/transactions/sales/{saleId}")
    suspend fun getSale(@Path("storeId") storeId: Long, @Path("saleId") saleId: Long): Response<Sale>

    @GET("/api/v1/stores/{storeId}/transactions/sales")
    suspend fun listSales(@Path("storeId") storeId: Long): Response<List<Sale>>

    @DELETE("/api/v1/stores/{storeId}/transactions/sales/{saleId}")
    suspend fun refundSale(@Path("storeId") storeId: Long, @Path("saleId") saleId: Long): Response<Void>

}