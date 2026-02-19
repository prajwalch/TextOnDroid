package com.prajwalch.textondroid.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import com.prajwalch.textondroid.domain.model.Theme

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    val enableDynamicTheme: Flow<Boolean> = dataStore
        .getOrDefault(key = ENABLE_DYNAMIC_THEME, default = true)

    val theme: Flow<Theme> = dataStore
        .getMapOrDefault(
            key = THEME,
            map = Theme::valueOf,
            default = Theme.Default
        )

    val enablePureBlack: Flow<Boolean> = dataStore
        .getOrDefault(key = ENABLE_PURE_BLACK, default = false)

    val wrapLines: Flow<Boolean> = dataStore
        .getOrDefault(key = WRAP_LINES, default = true)

    suspend fun enableDynamicTheme(enable: Boolean) {
        dataStore.setOrUpdate(key = ENABLE_DYNAMIC_THEME, value = enable)
    }

    suspend fun setTheme(theme: Theme) {
        dataStore.setOrUpdate(key = THEME, value = theme.name)
    }

    suspend fun enablePureBlack(enable: Boolean) {
        dataStore.setOrUpdate(key = ENABLE_PURE_BLACK, value = enable)
    }

    suspend fun setWrapLines(wrap: Boolean) {
        dataStore.setOrUpdate(key = WRAP_LINES, value = wrap)
    }

    private companion object {
        // Appearance
        val ENABLE_DYNAMIC_THEME = booleanPreferencesKey("enable_dynamic_theme")
        val THEME = stringPreferencesKey("theme")
        val ENABLE_PURE_BLACK = booleanPreferencesKey("enable_pure_black")

        // Editor
        val WRAP_LINES = booleanPreferencesKey("wrap_lines")
    }
}

/** Returns a pre-saved preferences or `default` if it doesn't exist. */
private fun <T> DataStore<Preferences>.getOrDefault(key: Preferences.Key<T>, default: T): Flow<T> {
    return data.map { preferences -> preferences[key] ?: default }
}

/**
 * Returns a pre-saved preferences after applying a function or `default`
 * if it doesn't exist.
 */
private fun <T, U> DataStore<Preferences>.getMapOrDefault(
    key: Preferences.Key<T>,
    map: (T) -> U,
    default: U,
): Flow<U> {
    return data.map { preferences -> preferences[key]?.let(map) ?: default }
}

/** Sets a preferences or updates if it already exists .*/
private suspend fun <T> DataStore<Preferences>.setOrUpdate(key: Preferences.Key<T>, value: T) {
    edit { preferences -> preferences[key] = value }
}