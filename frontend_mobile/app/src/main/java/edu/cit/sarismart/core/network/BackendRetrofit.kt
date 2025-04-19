package edu.cit.sarismart.core.network

import edu.cit.sarismart.BuildConfig
import edu.cit.sarismart.core.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Singleton
object BackendRetrofit {

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val backendRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BACKEND_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val instance: Retrofit
        get() = backendRetrofit

}