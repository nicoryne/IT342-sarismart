package edu.cit.sarismart.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class RefreshTokenManager @Inject constructor(@ApplicationContext private val context: Context): TokenManager {

    companion object {
        private val REFRESH_TOKEN_MANAGER = stringPreferencesKey("auth_access_token")
    }

    override val getToken: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_MANAGER]
        }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_MANAGER] = token
        }
    }

    override suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(REFRESH_TOKEN_MANAGER)
        }
    }
}