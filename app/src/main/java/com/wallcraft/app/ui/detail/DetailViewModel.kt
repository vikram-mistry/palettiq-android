package com.wallcraft.app.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallcraft.app.domain.model.Wallpaper
import com.wallcraft.app.domain.repository.FavouritesRepository
import com.wallcraft.app.domain.repository.WallpaperRepository
import com.wallcraft.app.generation.WallpaperGenerator
import com.wallcraft.app.util.WallpaperApplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val wallpaper: Wallpaper? = null,
    val isFavourite: Boolean = false,
    val isApplying: Boolean = false,
    val isDownloading: Boolean = false,
    val snackbarMessage: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wallpaperRepository: WallpaperRepository,
    private val favouritesRepository: FavouritesRepository,
    private val wallpaperGenerator: WallpaperGenerator,
    private val wallpaperApplier: WallpaperApplier
) : ViewModel() {
    
    private val wallpaperId: String = savedStateHandle.get<String>("wallpaperId") ?: ""
    
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()
    
    init {
        loadWallpaper()
    }
    
    private fun loadWallpaper() {
        viewModelScope.launch {
            val wallpaper = wallpaperRepository.getWallpaperById(wallpaperId)
            val isFav = favouritesRepository.isFavourite(wallpaperId)
            
            _uiState.update {
                it.copy(wallpaper = wallpaper, isFavourite = isFav)
            }
        }
    }
    
    fun toggleFavourite() {
        viewModelScope.launch {
            val wallpaper = _uiState.value.wallpaper ?: return@launch
            
            if (_uiState.value.isFavourite) {
                favouritesRepository.removeFromFavourites(wallpaper.id)
                _uiState.update { it.copy(isFavourite = false, snackbarMessage = "Removed from favourites") }
            } else {
                favouritesRepository.addToFavourites(wallpaper)
                _uiState.update { it.copy(isFavourite = true, snackbarMessage = "Added to favourites") }
            }
        }
    }
    
    fun applyWallpaper(target: WallpaperApplier.WallpaperTarget) {
        viewModelScope.launch {
            val wallpaper = _uiState.value.wallpaper ?: return@launch
            
            _uiState.update { it.copy(isApplying = true) }
            
            val bitmap = wallpaperGenerator.loadBitmap(wallpaper)
            if (bitmap != null) {
                val result = wallpaperApplier.applyWallpaper(bitmap, target)
                result.fold(
                    onSuccess = {
                        val targetName = when (target) {
                            WallpaperApplier.WallpaperTarget.HOME_SCREEN -> "home screen"
                            WallpaperApplier.WallpaperTarget.LOCK_SCREEN -> "lock screen"
                            WallpaperApplier.WallpaperTarget.BOTH -> "home and lock screen"
                        }
                        _uiState.update { 
                            it.copy(isApplying = false, snackbarMessage = "Applied to $targetName") 
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isApplying = false, snackbarMessage = "Failed: ${error.message}")
                        }
                    }
                )
                bitmap.recycle()
            } else {
                _uiState.update {
                    it.copy(isApplying = false, snackbarMessage = "Failed to load wallpaper")
                }
            }
        }
    }
    
    fun downloadWallpaper() {
        viewModelScope.launch {
            val wallpaper = _uiState.value.wallpaper ?: return@launch
            
            _uiState.update { it.copy(isDownloading = true) }
            
            val bitmap = wallpaperGenerator.loadBitmap(wallpaper)
            if (bitmap != null) {
                val fileName = "Palettiq_${wallpaper.category.name}_${System.currentTimeMillis()}"
                val result = wallpaperApplier.saveToDownloads(bitmap, fileName)
                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(isDownloading = false, snackbarMessage = "Saved to Downloads")
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isDownloading = false, snackbarMessage = "Failed: ${error.message}")
                        }
                    }
                )
                bitmap.recycle()
            } else {
                _uiState.update {
                    it.copy(isDownloading = false, snackbarMessage = "Failed to load wallpaper")
                }
            }
        }
    }
    
    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
