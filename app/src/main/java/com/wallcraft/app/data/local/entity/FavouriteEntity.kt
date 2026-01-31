package com.wallcraft.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing favourite wallpapers
 */
@Entity(tableName = "favourites")
data class FavouriteEntity(
    @PrimaryKey
    val wallpaperId: String,
    val category: String,
    val isDark: Boolean,
    val imagePath: String,
    val thumbnailPath: String,
    val seed: Long,
    val parametersJson: String, // Serialized WallpaperParameters
    val addedAt: Long = System.currentTimeMillis()
)
