package edu.cit.sarismart.features.user.tabs.stores.domain

import edu.cit.sarismart.features.user.tabs.stores.data.models.Product
import edu.cit.sarismart.features.user.tabs.stores.data.models.Report
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreReportsApiService {

    @GET("/api/v1/{storeId}/reports/daily")
    suspend fun dailySales(@Path("storeId") storeId: Long): Response<Report>

    @GET("/api/v1/{storeId}/reports/monthly")
    suspend fun monthlySales(@Path("storeId") storeId: Long): Response<Report>

    @GET("/api/v1/{storeId}/reports/inventory")
    suspend fun inventoryStatus(@Path("storeId") storeId: Long): Response<List<Product>>
    
}