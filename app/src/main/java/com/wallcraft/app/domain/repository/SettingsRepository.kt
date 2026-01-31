package com.wallcraft.app.domain.repository

import com.wallcraft.app.domain.model.AppSettings
import com.wallcraft.app.domain.model.AutoChangeInterval
import com.wallcraft.app.domain.model.GenerationFrequency
import com.wallcraft.app.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app settings operations
 */
interface SettingsRepository {
    val settings: Flow<AppSettings>
    
    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setAutoChangeEnabled(enabled: Boolean)
    suspend fun setAutoChangeInterval(interval: AutoChangeInterval)
    suspend fun setShowNotifications(enabled: Boolean)
    
    suspend fun updateTheme(theme: ThemeMode)
    suspend fun updateGenerationFrequency(frequency: GenerationFrequency)
    suspend fun updateAutoChange(enabled: Boolean, interval: AutoChangeInterval? = null)
    suspend fun updatePaletteLock(enabled: Boolean, colors: List<String> = emptyList())
    suspend fun updateLastGenerationDate(timestamp: Long)
    suspend fun incrementDailyGenerations()
    suspend fun resetDailyGenerations()
    suspend fun updateNotifications(enabled: Boolean)
}
