package edu.cit.sarismart.features.user.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cit.sarismart.features.user.tabs.stores.data.repository.StoreRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserNavigationHostViewModel @Inject constructor(
    private val storeRepository: StoreRepository
): ViewModel() {

    fun initStores() {
        viewModelScope.launch {
            try {
                storeRepository.updateStores()
            } catch (e: Exception) {
                Log.e("StoreOverviewScreenViewModel", "Error updating stores: ${e.message}")
            }
        }
    }
}