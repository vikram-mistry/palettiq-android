package com.wallcraft.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallcraft.app.domain.model.AppSettings
import com.wallcraft.app.domain.model.AutoChangeInterval
import com.wallcraft.app.domain.model.ThemeMode
import com.wallcraft.app.domain.repository.FavouritesRepository
import com.wallcraft.app.domain.repository.SettingsRepository
import com.wallcraft.app.domain.repository.WallpaperRepository
import com.wallcraft.app.generation.WallpaperGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val cacheSize: String = "0 MB",
    val isClearing: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val wallpaperRepository: WallpaperRepository,
    private val favouritesRepository: FavouritesRepository,
    private val wallpaperGenerator: WallpaperGenerator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        loadCacheSize()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }
    
    private fun loadCacheSize() {
        val sizeBytes = wallpaperGenerator.getCacheSize()
        val sizeMB = sizeBytes / (1024f * 1024f)
        _uiState.update { it.copy(cacheSize = "%.1f MB".format(sizeMB)) }
    }
    
    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }
    
    fun setAutoChangeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoChangeEnabled(enabled)
        }
    }
    
    fun setAutoChangeInterval(interval: AutoChangeInterval) {
        viewModelScope.launch {
            settingsRepository.setAutoChangeInterval(interval)
        }
    }
    
    fun setShowNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowNotifications(enabled)
        }
    }
    
    fun clearCache() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearing = true) }
            
            // Clear file cache, wallpapers DB, AND favourites
            wallpaperGenerator.clearCache()
            wallpaperRepository.deleteAllWallpapers()
            favouritesRepository.clearAllFavourites()
            
            loadCacheSize()
            _uiState.update { it.copy(isClearing = false) }
        }
    }
}
