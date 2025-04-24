package edu.cit.sarismart.core.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackendRetrofitClient()

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiRetrofitClient()

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UpcRetrofitClient()
