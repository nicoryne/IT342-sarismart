package edu.cit.sarismart.features.user.tabs.account.data.repository


interface AccountRepository {

    suspend fun updateAccountDetails(name: String, mobileNumber: String)

    suspend fun getUserId(): String

    suspend fun getUserEmail(): String

    suspend fun getUserFullName(): String

    suspend fun getUserPhone(): String

}