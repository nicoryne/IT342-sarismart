package edu.cit.sarismart.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStoresManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_stores_prefs")

    private val dataStore = context.dataStore

    private val userOwnerStoresKey = stringPreferencesKey("user_owner_stores")

    private val userWorkerStoresKey = stringPreferencesKey("user_worker_stores")

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveUserOwnerStores(stores: List<Store>) {
        val storesJson = gson.toJson(stores)
        dataStore.edit { preferences ->
            preferences[userOwnerStoresKey] = storesJson
        }
    }

    suspend fun saveUserWorkerStores(stores: List<Store>) {
        val storesJson = gson.toJson(stores)
        dataStore.edit { preferences ->
            preferences[userWorkerStoresKey] = storesJson
        }
    }

    suspend fun addToUserOwnerStores(store: Store) {
        val stores = getUserOwnerStores().first().toMutableList()
        stores.add(store)
        saveUserOwnerStores(stores)
    }

    suspend fun addToUserWorkerStores(store: Store) {
        val stores = getUserWorkerStores().first().toMutableList()
        stores.add(store)
        saveUserWorkerStores(stores)
    }

    suspend fun deleteFromUserOwnerStores(id: Long) {
        val stores = getUserOwnerStores().first().toMutableList()
        stores.removeIf { it.id == id }
        saveUserOwnerStores(stores)
    }

    suspend fun deleteFromUserWorkerStores(id: Long) {
        val stores = getUserWorkerStores().first().toMutableList()
        stores.removeIf { it.id == id }
        saveUserWorkerStores(stores)
    }

    fun getUserOwnerStores(): Flow<List<Store>> {
        return dataStore.data
            .map { preferences ->
                val storesJson = preferences[userOwnerStoresKey] ?: ""
                if (storesJson.isNotEmpty()) {
                    val typeToken = object : TypeToken<List<Store>>() {}.type
                    gson.fromJson(storesJson, typeToken) ?: emptyList()
                } else {
                    emptyList()
                }
            }
    }

    fun getUserWorkerStores(): Flow<List<Store>> {
        return dataStore.data
            .map { preferences ->
                val storesJson = preferences[userWorkerStoresKey] ?: ""
                if (storesJson.isNotEmpty()) {
                    val typeToken = object : TypeToken<List<Store>>() {}.type
                    gson.fromJson(storesJson, typeToken) ?: emptyList()
                } else {
                    emptyList()
                }
            }
    }


}