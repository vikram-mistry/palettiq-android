package com.wallcraft.app.data.repository

import com.wallcraft.app.data.local.dao.WallpaperDao
import com.wallcraft.app.data.local.entity.WallpaperEntity
import com.wallcraft.app.domain.model.GenerationType
import com.wallcraft.app.domain.model.Wallpaper
import com.wallcraft.app.domain.model.WallpaperCategory
import com.wallcraft.app.domain.model.WallpaperParameters
import com.wallcraft.app.domain.repository.WallpaperRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperRepositoryImpl @Inject constructor(
    private val wallpaperDao: WallpaperDao
) : WallpaperRepository {
    
    override fun getAllWallpapers(): Flow<List<Wallpaper>> {
        return wallpaperDao.getAllWallpapers().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getDarkWallpapers(): Flow<List<Wallpaper>> {
        return wallpaperDao.getDarkWallpapers().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getLightWallpapers(): Flow<List<Wallpaper>> {
        return wallpaperDao.getLightWallpapers().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getWallpapersByCategory(category: WallpaperCategory): Flow<List<Wallpaper>> {
        return wallpaperDao.getWallpapersByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getWallpaperById(id: String): Wallpaper? {
        return wallpaperDao.getWallpaperById(id)?.toDomain()
    }
    
    override suspend fun saveWallpaper(wallpaper: Wallpaper) {
        wallpaperDao.insertWallpaper(wallpaper.toEntity())
    }
    
    override suspend fun saveWallpapers(wallpapers: List<Wallpaper>) {
        wallpaperDao.insertWallpapers(wallpapers.map { it.toEntity() })
    }
    
    override suspend fun deleteWallpaper(id: String) {
        wallpaperDao.deleteWallpaper(id)
    }
    
    override suspend fun deleteAllWallpapers() {
        wallpaperDao.deleteAllWallpapers()
    }
    
    override suspend fun getWallpapersCount(): Int {
        return wallpaperDao.getWallpapersCount()
    }
    
    // Extension functions for mapping
    private fun WallpaperEntity.toDomain(): Wallpaper {
        return Wallpaper(
            id = id,
            category = parseCategory(category),
            isDark = isDark,
            generationType = GenerationType.valueOf(generationType),
            createdAt = createdAt,
            imagePath = imagePath,
            thumbnailPath = thumbnailPath,
            seed = seed,
            parameters = parseParameters(parametersJson),
            isFavourite = false
        )
    }
    
    private fun parseCategory(categoryName: String): WallpaperCategory {
        return when (categoryName.uppercase()) {
            // Handle legacy enum names
            "CARTOON" -> WallpaperCategory.SURPRISE
            "AI_COMING_SOON" -> WallpaperCategory.SURPRISE
            else -> try {
                WallpaperCategory.valueOf(categoryName)
            } catch (e: IllegalArgumentException) {
                WallpaperCategory.GRADIENT // Fallback
            }
        }
    }
    
    private fun Wallpaper.toEntity(): WallpaperEntity {
        return WallpaperEntity(
            id = id,
            category = category.name,
            isDark = isDark,
            generationType = generationType.name,
            createdAt = createdAt,
            imagePath = imagePath,
            thumbnailPath = thumbnailPath,
            seed = seed,
            parametersJson = serializeParameters(parameters)
        )
    }
    
    private fun parseParameters(json: String): WallpaperParameters {
        return try {
            // Simple parsing - in production use proper JSON library
            WallpaperParameters()
        } catch (e: Exception) {
            WallpaperParameters()
        }
    }
    
    private fun serializeParameters(params: WallpaperParameters): String {
        return "{}"
    }
}
