package com.wallcraft.app.domain.repository

import com.wallcraft.app.domain.model.Wallpaper
import com.wallcraft.app.domain.model.WallpaperCategory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for wallpaper operations
 */
interface WallpaperRepository {
    fun getAllWallpapers(): Flow<List<Wallpaper>>
    fun getDarkWallpapers(): Flow<List<Wallpaper>>
    fun getLightWallpapers(): Flow<List<Wallpaper>>
    fun getWallpapersByCategory(category: WallpaperCategory): Flow<List<Wallpaper>>
    suspend fun getWallpaperById(id: String): Wallpaper?
    suspend fun saveWallpaper(wallpaper: Wallpaper)
    suspend fun saveWallpapers(wallpapers: List<Wallpaper>)
    suspend fun deleteWallpaper(id: String)
    suspend fun deleteAllWallpapers()
    suspend fun getWallpapersCount(): Int
}
