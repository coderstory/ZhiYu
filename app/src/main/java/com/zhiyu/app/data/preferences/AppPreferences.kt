package com.zhiyu.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zhiyu.app.model.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppPreferences(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LAST_ACTIVE_TAB = stringPreferencesKey("last_active_tab")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            val raw = preferences[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(raw)
            } catch (_: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
        }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    val lastActiveTab: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.LAST_ACTIVE_TAB] ?: "info"
        }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.IS_FIRST_LAUNCH] ?: true
        }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun setLastActiveTab(tab: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.LAST_ACTIVE_TAB] = tab
        }
    }

    suspend fun markLaunched() {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_FIRST_LAUNCH] = false
        }
    }
}
