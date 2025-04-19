package edu.cit.sarismart.features.user.tabs.account.data.repository

import edu.cit.sarismart.features.user.tabs.account.domain.AccountService
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(private val accountService: AccountService): AccountRepository {

    override suspend fun updateAccountDetails(
        name: String,
        mobileNumber: String
    ) {
        TODO("Not yet implemented")
    }


}