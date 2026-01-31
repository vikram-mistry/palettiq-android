package com.wallcraft.app.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallcraft.app.domain.model.ColorPalette
import com.wallcraft.app.domain.model.Wallpaper
import com.wallcraft.app.domain.model.WallpaperCategory
import com.wallcraft.app.domain.repository.FavouritesRepository
import com.wallcraft.app.domain.repository.WallpaperRepository
import com.wallcraft.app.data.local.datastore.SettingsDataStore
import com.wallcraft.app.generation.WallpaperGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscoverUiState(
    val wallpapers: List<Wallpaper> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val selectedFilter: WallpaperFilter = WallpaperFilter.ALL,
    val selectedPalette: ColorPalette = ColorPalette.RANDOM,
    val error: String? = null,
    val generatingMessage: String? = null
)

enum class WallpaperFilter(val displayName: String) {
    ALL("All"),
    LIGHT("Light"),
    DARK("Dark")
}

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val wallpaperRepository: WallpaperRepository,
    private val favouritesRepository: FavouritesRepository,
    private val wallpaperGenerator: WallpaperGenerator,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()
    
    private val favouriteIds = favouritesRepository.getAllFavouriteIds()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    init {
        loadWallpapers()
        loadSelectedPalette()
    }
    
    private fun loadSelectedPalette() {
        viewModelScope.launch {
            settingsDataStore.getSelectedPalette().collect { palette ->
                _uiState.update { it.copy(selectedPalette = palette) }
            }
        }
    }
    
    fun setSelectedPalette(palette: ColorPalette) {
        viewModelScope.launch {
            settingsDataStore.updateSelectedPalette(palette)
            _uiState.update { it.copy(selectedPalette = palette) }
        }
    }
    
    private fun loadWallpapers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Check if we need to generate initial wallpapers
                val count = wallpaperRepository.getWallpapersCount()
                if (count == 0) {
                    generateInitialWallpapers()
                }
                
                // Observe wallpapers with favourite status
                combine(
                    wallpaperRepository.getAllWallpapers(),
                    favouriteIds
                ) { wallpapers, favIds ->
                    wallpapers.map { wallpaper ->
                        wallpaper.copy(isFavourite = favIds.contains(wallpaper.id))
                    }
                }.collect { wallpapersWithFavStatus ->
                    val filtered = filterWallpapers(wallpapersWithFavStatus, _uiState.value.selectedFilter)
                    _uiState.update { 
                        it.copy(
                            wallpapers = filtered, 
                            isLoading = false,
                            isRefreshing = false,
                            generatingMessage = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message
                    ) 
                }
            }
        }
    }
    
    private suspend fun generateInitialWallpapers() {
        // Generate a variety of wallpapers on first launch
        val categoriesToGenerate = listOf(
            WallpaperCategory.SURPRISE,
            WallpaperCategory.GRADIENT,
            WallpaperCategory.PASTEL,
            WallpaperCategory.GEOMETRIC,
            WallpaperCategory.BOKEH,
            WallpaperCategory.FLUID
        )
        
        _uiState.update { it.copy(generatingMessage = "Creating your wallpapers...") }
        
        for ((index, category) in categoriesToGenerate.withIndex()) {
            _uiState.update { 
                it.copy(generatingMessage = "Generating ${category.displayName} (${index + 1}/${categoriesToGenerate.size * 2})...") 
            }
            
            // Generate light version
            val lightWallpaper = wallpaperGenerator.generate(category, isDark = false)
            wallpaperRepository.saveWallpaper(lightWallpaper)
            
            // Generate dark version
            val darkWallpaper = wallpaperGenerator.generate(category, isDark = true)
            wallpaperRepository.saveWallpaper(darkWallpaper)
        }
    }
    
    fun setFilter(filter: WallpaperFilter) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedFilter = filter) }
            
            val currentWallpapers = when (filter) {
                WallpaperFilter.LIGHT -> wallpaperRepository.getLightWallpapers().first()
                WallpaperFilter.DARK -> wallpaperRepository.getDarkWallpapers().first()
                WallpaperFilter.ALL -> wallpaperRepository.getAllWallpapers().first()
            }
            
            val favIds = favouriteIds.value
            val withFavStatus = currentWallpapers.map { 
                it.copy(isFavourite = favIds.contains(it.id)) 
            }
            
            _uiState.update { it.copy(wallpapers = withFavStatus) }
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            
            // Generate new wallpapers - exclude AI_COMING_SOON
            val category = WallpaperCategory.values().random()
            val isDark = kotlin.random.Random.nextBoolean()
            
            val palette = _uiState.value.selectedPalette
            val paletteColors = if (palette != ColorPalette.RANDOM && palette.colors.isNotEmpty()) {
                palette.colors.map { String.format("#%06X", 0xFFFFFF and it) }
            } else {
                emptyList()
            }
            
            val newWallpaper = wallpaperGenerator.generate(category, isDark, lockedColors = paletteColors)
            wallpaperRepository.saveWallpaper(newWallpaper)
            
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
    
    /**
     * Generate a specific wallpaper by category with optional color palette
     */
    fun generateWallpaper(category: WallpaperCategory, isDark: Boolean, palette: ColorPalette = ColorPalette.RANDOM) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    generatingMessage = "Generating ${category.displayName}..."
                ) 
            }
            
            try {
                // Convert palette to color hex strings if not random
                val paletteColors = if (palette != ColorPalette.RANDOM && palette.colors.isNotEmpty()) {
                    palette.colors.map { String.format("#%06X", 0xFFFFFF and it) }
                } else {
                    emptyList()
                }
                
                val newWallpaper = wallpaperGenerator.generate(category, isDark, lockedColors = paletteColors)
                wallpaperRepository.saveWallpaper(newWallpaper)
                
                _uiState.update { 
                    it.copy(isLoading = false, generatingMessage = null) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Failed to generate: ${e.message}",
                        generatingMessage = null
                    ) 
                }
            }
        }
    }
    
    fun toggleFavourite(wallpaper: Wallpaper) {
        viewModelScope.launch {
            if (wallpaper.isFavourite) {
                favouritesRepository.removeFromFavourites(wallpaper.id)
            } else {
                favouritesRepository.addToFavourites(wallpaper)
            }
        }
    }
    
    private fun filterWallpapers(wallpapers: List<Wallpaper>, filter: WallpaperFilter): List<Wallpaper> {
        return when (filter) {
            WallpaperFilter.ALL -> wallpapers
            WallpaperFilter.LIGHT -> wallpapers.filter { !it.isDark }
            WallpaperFilter.DARK -> wallpapers.filter { it.isDark }
        }
    }
}
