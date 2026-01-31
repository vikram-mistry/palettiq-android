package com.wallcraft.app.data.repository

import com.wallcraft.app.data.local.datastore.SettingsDataStore
import com.wallcraft.app.domain.model.AppSettings
import com.wallcraft.app.domain.model.AutoChangeInterval
import com.wallcraft.app.domain.model.GenerationFrequency
import com.wallcraft.app.domain.model.ThemeMode
import com.wallcraft.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {
    
    override val settings: Flow<AppSettings> = settingsDataStore.settings
    
    override suspend fun setThemeMode(themeMode: ThemeMode) {
        settingsDataStore.updateTheme(themeMode)
    }
    
    override suspend fun setAutoChangeEnabled(enabled: Boolean) {
        settingsDataStore.updateAutoChange(enabled, null)
    }
    
    override suspend fun setAutoChangeInterval(interval: AutoChangeInterval) {
        settingsDataStore.updateAutoChange(true, interval)
    }
    
    override suspend fun setShowNotifications(enabled: Boolean) {
        settingsDataStore.updateNotifications(enabled)
    }
    
    override suspend fun updateTheme(theme: ThemeMode) {
        settingsDataStore.updateTheme(theme)
    }
    
    override suspend fun updateGenerationFrequency(frequency: GenerationFrequency) {
        settingsDataStore.updateGenerationFrequency(frequency)
    }
    
    override suspend fun updateAutoChange(enabled: Boolean, interval: AutoChangeInterval?) {
        settingsDataStore.updateAutoChange(enabled, interval)
    }
    
    override suspend fun updatePaletteLock(enabled: Boolean, colors: List<String>) {
        settingsDataStore.updatePaletteLock(enabled, colors)
    }
    
    override suspend fun updateLastGenerationDate(timestamp: Long) {
        settingsDataStore.updateLastGenerationDate(timestamp)
    }
    
    override suspend fun incrementDailyGenerations() {
        settingsDataStore.incrementDailyGenerations()
    }
    
    override suspend fun resetDailyGenerations() {
        settingsDataStore.resetDailyGenerations()
    }
    
    override suspend fun updateNotifications(enabled: Boolean) {
        settingsDataStore.updateNotifications(enabled)
    }
}
