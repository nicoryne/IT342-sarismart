package edu.cit.sarismart.features.user.tabs.stores.data.models

import java.time.LocalDateTime

data class Sale (
    val id: Long,
    val store: Store,
    val totalAmount: Double,
    val saleDate: LocalDateTime
)