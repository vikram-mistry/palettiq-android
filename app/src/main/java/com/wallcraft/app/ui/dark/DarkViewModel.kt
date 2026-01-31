package com.wallcraft.app.ui.dark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallcraft.app.domain.model.Wallpaper
import com.wallcraft.app.domain.model.WallpaperCategory
import com.wallcraft.app.domain.repository.FavouritesRepository
import com.wallcraft.app.domain.repository.WallpaperRepository
import com.wallcraft.app.generation.WallpaperGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DarkUiState(
    val wallpapers: List<Wallpaper> = emptyList(),
    val isLoading: Boolean = true,
    val isGenerating: Boolean = false
)

@HiltViewModel
class DarkViewModel @Inject constructor(
    private val wallpaperRepository: WallpaperRepository,
    private val favouritesRepository: FavouritesRepository,
    private val wallpaperGenerator: WallpaperGenerator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DarkUiState())
    val uiState: StateFlow<DarkUiState> = _uiState.asStateFlow()
    
    private val favouriteIds = favouritesRepository.getAllFavouriteIds()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    init {
        loadDarkWallpapers()
    }
    
    private fun loadDarkWallpapers() {
        viewModelScope.launch {
            combine(
                wallpaperRepository.getDarkWallpapers(),
                favouriteIds
            ) { wallpapers, favIds ->
                wallpapers.map { it.copy(isFavourite = favIds.contains(it.id)) }
            }.collect { withFavStatus ->
                _uiState.update { 
                    it.copy(wallpapers = withFavStatus, isLoading = false)
                }
            }
        }
    }
    
    fun generateAMOLED() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            
            // Generate AMOLED and other dark wallpapers
            val amoled = wallpaperGenerator.generate(WallpaperCategory.AMOLED, isDark = true)
            wallpaperRepository.saveWallpaper(amoled)
            
            _uiState.update { it.copy(isGenerating = false) }
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
}
