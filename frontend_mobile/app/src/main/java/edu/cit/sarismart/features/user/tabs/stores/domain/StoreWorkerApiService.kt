package edu.cit.sarismart.features.user.tabs.stores.domain

import edu.cit.sarismart.features.user.tabs.account.data.models.User
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StoreWorkerApiService {

    @POST("/api/v1/stores/$[storeId}/workers/{workerId}")
    suspend fun assignWorker(@Path("storeId") storeId: Long, @Path("workerId") workerId: Long): Response<Void>

    @DELETE("/api/v1/stores/$[storeId}/workers/{workerId}")
    suspend fun removeWorker(@Path("storeId") storeId: Long, @Path("workerId") workerId: Long): Response<Void>

    @GET("/api/v1/stores/$[storeId}/workers")
    suspend fun listWorkers(@Path("storeId") storeId: Long): Response<List<User>>

}