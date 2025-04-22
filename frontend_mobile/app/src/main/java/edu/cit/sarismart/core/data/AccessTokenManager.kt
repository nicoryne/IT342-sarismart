package edu.cit.sarismart.core.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AccessTokenManager @Inject constructor(@ApplicationContext private val context: Context): TokenManager {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "access_token_prefs")

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("auth_access_token")
        private val EXPIRES_AT_KEY = stringSetPreferencesKey("auth_access_token_expires_at")
    }

    override val getToken: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }

    val getExpiresAt: Flow<Long?>
        get() = context.dataStore.data.map { preferences ->
            preferences[EXPIRES_AT_KEY]?.firstOrNull()?.toLongOrNull()
        }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    override suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
        }
    }

    suspend fun setExpiresAt(expiresAt: Long) {
        context.dataStore.edit { preferences ->
            preferences[EXPIRES_AT_KEY] = setOf(expiresAt.toString())
        }
    }

    suspend fun isExpired(): Boolean {
        val currentTimestampSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        val expiresAt = getExpiresAt.first()
        Log.d("AccessTokenManager", "Current timestamp: $currentTimestampSeconds, Expires at: $expiresAt")
        return expiresAt == null || expiresAt.toLong() < currentTimestampSeconds
    }


}
