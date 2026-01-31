package com.wallcraft.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching generated wallpapers metadata
 */
@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey
    val id: String,
    val category: String,
    val isDark: Boolean,
    val generationType: String,
    val createdAt: Long,
    val imagePath: String,
    val thumbnailPath: String,
    val seed: Long,
    val parametersJson: String
)
