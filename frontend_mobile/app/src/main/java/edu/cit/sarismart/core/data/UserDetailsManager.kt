package edu.cit.sarismart.core.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.cit.sarismart.features.user.tabs.account.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserDetailsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_details_prefs")

    private val dataStore = context.dataStore

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_PHONE = stringPreferencesKey("user_phone")
    }

    val getUserId: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }

    val getUserEmail: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL]
        }

    val getUserName: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[USER_NAME]
        }

    val getUserPhone: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[USER_PHONE]
        }

    suspend fun saveUserDetails(
        user: User
    ) {

        dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_EMAIL] = user.email
        }

        if (user.fullName != null) {
            dataStore.edit { preferences ->
                preferences[USER_NAME] = user.fullName
            }
        }

        if (user.phone != null) {
            dataStore.edit { preferences ->
                preferences[USER_PHONE] = user.phone
            }
        }
    }

    suspend fun updateUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun updateUserPhone(phone: String) {
        dataStore.edit { preferences ->
            preferences[USER_PHONE] = phone
        }
    }

}