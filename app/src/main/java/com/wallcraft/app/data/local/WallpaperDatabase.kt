package com.wallcraft.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wallcraft.app.data.local.dao.FavouritesDao
import com.wallcraft.app.data.local.dao.WallpaperDao
import com.wallcraft.app.data.local.entity.FavouriteEntity
import com.wallcraft.app.data.local.entity.WallpaperEntity

/**
 * Room database for Palettiq app
 */
@Database(
    entities = [FavouriteEntity::class, WallpaperEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WallpaperDatabase : RoomDatabase() {
    abstract fun favouritesDao(): FavouritesDao
    abstract fun wallpaperDao(): WallpaperDao
    
    companion object {
        const val DATABASE_NAME = "palettiq_database"
    }
}
