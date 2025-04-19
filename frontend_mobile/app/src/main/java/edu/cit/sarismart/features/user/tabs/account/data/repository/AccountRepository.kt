package edu.cit.sarismart.features.user.tabs.account.data.repository


interface AccountRepository {

    suspend fun updateAccountDetails(name: String, mobileNumber: String)

}