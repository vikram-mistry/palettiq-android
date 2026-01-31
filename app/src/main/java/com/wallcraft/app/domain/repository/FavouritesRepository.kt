package com.wallcraft.app.domain.repository

import com.wallcraft.app.domain.model.Wallpaper
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for favourite wallpapers operations
 */
interface FavouritesRepository {
    fun getAllFavourites(): Flow<List<Wallpaper>>
    fun getDarkFavourites(): Flow<List<Wallpaper>>
    fun getLightFavourites(): Flow<List<Wallpaper>>
    fun getAllFavouriteIds(): Flow<List<String>>
    suspend fun isFavourite(wallpaperId: String): Boolean
    suspend fun addToFavourites(wallpaper: Wallpaper)
    suspend fun removeFromFavourites(wallpaperId: String)
    suspend fun getFavouritesCount(): Int
    suspend fun clearAllFavourites()
}
