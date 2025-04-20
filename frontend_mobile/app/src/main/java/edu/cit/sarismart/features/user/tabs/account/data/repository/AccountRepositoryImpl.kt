package edu.cit.sarismart.features.user.tabs.account.data.repository

import edu.cit.sarismart.core.data.UserDetailsManager
import edu.cit.sarismart.features.user.tabs.account.domain.AccountService
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(private val accountService: AccountService, private val userDetailsManager: UserDetailsManager): AccountRepository {

    override suspend fun updateAccountDetails(
        name: String,
        mobileNumber: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserId(): String {
        return userDetailsManager.getUserId.first() ?: ""
    }

    override suspend fun getUserEmail(): String {
        return userDetailsManager.getUserEmail.first() ?: ""
    }

    override suspend fun getUserFullName(): String {
        return userDetailsManager.getUserName.first() ?: ""
    }

    override suspend fun getUserPhone(): String {
        return userDetailsManager.getUserPhone.first() ?: ""
    }


}