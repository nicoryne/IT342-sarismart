package edu.cit.sarismart.core.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.data.AccessTokenManager
import edu.cit.sarismart.core.network.BackendRetrofitClient
import edu.cit.sarismart.core.network.GeminiRetrofitClient
import edu.cit.sarismart.core.util.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    @BackendRetrofitClient
    fun provideBackendOkHttpClient(
        accessTokenManager: AccessTokenManager,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val headerInterceptor = Interceptor { chain ->
            val token = runBlocking { accessTokenManager.getToken.first() }
            val request = chain.request().newBuilder()
                .apply {
                    if (!token.isNullOrEmpty()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }
                .build()
            chain.proceed(request)
        }
            return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @GeminiRetrofitClient
    fun provideGeminiOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @BackendRetrofitClient
    fun provideBackendRetrofit(
        @BackendRetrofitClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BACKEND_API_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @GeminiRetrofitClient
    fun provideGeminiRetrofit(
        @GeminiRetrofitClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.GEMINI_API_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
