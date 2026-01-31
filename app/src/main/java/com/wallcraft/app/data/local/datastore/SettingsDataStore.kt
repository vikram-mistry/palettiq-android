package com.wallcraft.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.wallcraft.app.domain.model.AppSettings
import com.wallcraft.app.domain.model.AutoChangeInterval
import com.wallcraft.app.domain.model.ColorPalette
import com.wallcraft.app.domain.model.GenerationFrequency
import com.wallcraft.app.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * DataStore-based settings persistence
 */
@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    
    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        val GENERATION_FREQUENCY = stringPreferencesKey("generation_frequency")
        val AUTO_CHANGE_ENABLED = booleanPreferencesKey("auto_change_enabled")
        val AUTO_CHANGE_INTERVAL = stringPreferencesKey("auto_change_interval")
        val PALETTE_LOCK_ENABLED = booleanPreferencesKey("palette_lock_enabled")
        val LOCKED_PALETTE = stringPreferencesKey("locked_palette") // JSON array
        val SELECTED_PALETTE = stringPreferencesKey("selected_palette")
        val LAST_GENERATION_DATE = longPreferencesKey("last_generation_date")
        val DAILY_GENERATIONS_COUNT = intPreferencesKey("daily_generations_count")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }
    
    val settings: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppSettings(
                themeMode = preferences[PreferencesKeys.THEME]?.let { 
                    ThemeMode.valueOf(it) 
                } ?: ThemeMode.SYSTEM,
                generationFrequency = preferences[PreferencesKeys.GENERATION_FREQUENCY]?.let { 
                    GenerationFrequency.valueOf(it) 
                } ?: GenerationFrequency.DAILY,
                autoChangeEnabled = preferences[PreferencesKeys.AUTO_CHANGE_ENABLED] ?: false,
                autoChangeInterval = preferences[PreferencesKeys.AUTO_CHANGE_INTERVAL]?.let { 
                    AutoChangeInterval.valueOf(it) 
                } ?: AutoChangeInterval.DAILY,
                paletteLockEnabled = preferences[PreferencesKeys.PALETTE_LOCK_ENABLED] ?: false,
                lockedPalette = parseColorList(preferences[PreferencesKeys.LOCKED_PALETTE]),
                lastGenerationDate = preferences[PreferencesKeys.LAST_GENERATION_DATE] ?: 0L,
                dailyGenerationsCount = preferences[PreferencesKeys.DAILY_GENERATIONS_COUNT] ?: 0,
                showNotifications = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
            )
        }
    
    suspend fun updateTheme(theme: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }
    
    suspend fun updateGenerationFrequency(frequency: GenerationFrequency) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GENERATION_FREQUENCY] = frequency.name
        }
    }
    
    suspend fun updateAutoChange(enabled: Boolean, interval: AutoChangeInterval? = null) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_CHANGE_ENABLED] = enabled
            interval?.let { preferences[PreferencesKeys.AUTO_CHANGE_INTERVAL] = it.name }
        }
    }
    
    suspend fun updatePaletteLock(enabled: Boolean, colors: List<String> = emptyList()) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PALETTE_LOCK_ENABLED] = enabled
            preferences[PreferencesKeys.LOCKED_PALETTE] = colors.joinToString(",")
        }
    }
    
    suspend fun updateLastGenerationDate(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_GENERATION_DATE] = timestamp
        }
    }
    
    suspend fun incrementDailyGenerations() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.DAILY_GENERATIONS_COUNT] ?: 0
            preferences[PreferencesKeys.DAILY_GENERATIONS_COUNT] = current + 1
        }
    }
    
    suspend fun resetDailyGenerations() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_GENERATIONS_COUNT] = 0
        }
    }
    
    suspend fun updateNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    suspend fun updateSelectedPalette(palette: ColorPalette) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_PALETTE] = palette.name
        }
    }
    
    fun getSelectedPalette(): Flow<ColorPalette> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_PALETTE]?.let {
                try { ColorPalette.valueOf(it) } catch (e: Exception) { ColorPalette.RANDOM }
            } ?: ColorPalette.RANDOM
        }
    
    private fun parseColorList(value: String?): List<String> {
        return value?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }
}
