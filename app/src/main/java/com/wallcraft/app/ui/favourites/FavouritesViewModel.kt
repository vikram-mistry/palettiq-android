package com.wallcraft.app.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallcraft.app.domain.model.Wallpaper
import com.wallcraft.app.domain.repository.FavouritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavouritesUiState(
    val favourites: List<Wallpaper> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val favouritesRepository: FavouritesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavouritesUiState())
    val uiState: StateFlow<FavouritesUiState> = _uiState.asStateFlow()
    
    init {
        loadFavourites()
    }
    
    private fun loadFavourites() {
        viewModelScope.launch {
            favouritesRepository.getAllFavourites().collect { favourites ->
                _uiState.update { 
                    it.copy(
                        favourites = favourites.map { fav -> fav.copy(isFavourite = true) },
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun removeFromFavourites(wallpaper: Wallpaper) {
        viewModelScope.launch {
            favouritesRepository.removeFromFavourites(wallpaper.id)
        }
    }
}
