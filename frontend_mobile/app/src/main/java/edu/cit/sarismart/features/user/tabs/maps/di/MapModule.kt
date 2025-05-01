package edu.cit.sarismart.features.user.tabs.maps.di

import edu.cit.sarismart.features.user.tabs.maps.domain.StoreMapApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.cit.sarismart.core.network.BackendRetrofitClient
import edu.cit.sarismart.features.user.tabs.maps.data.MapRepository
import edu.cit.sarismart.features.user.tabs.maps.data.MapRepositoryImpl
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapModule {

    @Provides
    @Singleton
    fun provideMapRepository(
        @BackendRetrofitClient backendRetrofit: Retrofit): MapRepository {
        return MapRepositoryImpl(backendRetrofit.create<StoreMapApiService>(StoreMapApiService::class.java))
    }

}