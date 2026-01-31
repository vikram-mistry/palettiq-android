package com.wallcraft.app.data.local.dao

import androidx.room.*
import com.wallcraft.app.data.local.entity.WallpaperEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for wallpapers cache
 */
@Dao
interface WallpaperDao {
    
    @Query("SELECT * FROM wallpapers ORDER BY createdAt DESC")
    fun getAllWallpapers(): Flow<List<WallpaperEntity>>
    
    @Query("SELECT * FROM wallpapers WHERE isDark = 1 ORDER BY createdAt DESC")
    fun getDarkWallpapers(): Flow<List<WallpaperEntity>>
    
    @Query("SELECT * FROM wallpapers WHERE isDark = 0 ORDER BY createdAt DESC")
    fun getLightWallpapers(): Flow<List<WallpaperEntity>>
    
    @Query("SELECT * FROM wallpapers WHERE category = :category ORDER BY createdAt DESC")
    fun getWallpapersByCategory(category: String): Flow<List<WallpaperEntity>>
    
    @Query("SELECT * FROM wallpapers WHERE id = :id")
    suspend fun getWallpaperById(id: String): WallpaperEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpaper(wallpaper: WallpaperEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpapers(wallpapers: List<WallpaperEntity>)
    
    @Query("DELETE FROM wallpapers WHERE id = :id")
    suspend fun deleteWallpaper(id: String)
    
    @Query("DELETE FROM wallpapers")
    suspend fun deleteAllWallpapers()
    
    @Query("SELECT COUNT(*) FROM wallpapers")
    suspend fun getWallpapersCount(): Int
}
