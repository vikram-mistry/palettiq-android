package com.wallcraft.app.data.repository

import com.wallcraft.app.data.local.dao.FavouritesDao
import com.wallcraft.app.data.local.entity.FavouriteEntity
import com.wallcraft.app.domain.model.GenerationType
import com.wallcraft.app.domain.model.Wallpaper
import com.wallcraft.app.domain.model.WallpaperCategory
import com.wallcraft.app.domain.model.WallpaperParameters
import com.wallcraft.app.domain.repository.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesRepositoryImpl @Inject constructor(
    private val favouritesDao: FavouritesDao
) : FavouritesRepository {
    
    override fun getAllFavourites(): Flow<List<Wallpaper>> {
        return favouritesDao.getAllFavourites().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getDarkFavourites(): Flow<List<Wallpaper>> {
        return favouritesDao.getDarkFavourites().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getLightFavourites(): Flow<List<Wallpaper>> {
        return favouritesDao.getLightFavourites().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getAllFavouriteIds(): Flow<List<String>> {
        return favouritesDao.getAllFavouriteIds()
    }
    
    override suspend fun isFavourite(wallpaperId: String): Boolean {
        return favouritesDao.isFavourite(wallpaperId)
    }
    
    override suspend fun addToFavourites(wallpaper: Wallpaper) {
        favouritesDao.insertFavourite(wallpaper.toFavouriteEntity())
    }
    
    override suspend fun removeFromFavourites(wallpaperId: String) {
        favouritesDao.deleteFavourite(wallpaperId)
    }
    
    override suspend fun getFavouritesCount(): Int {
        return favouritesDao.getFavouritesCount()
    }
    
    override suspend fun clearAllFavourites() {
        favouritesDao.deleteAllFavourites()
    }
    
    private fun FavouriteEntity.toDomain(): Wallpaper {
        return Wallpaper(
            id = wallpaperId,
            category = parseCategory(category),
            isDark = isDark,
            generationType = GenerationType.PROCEDURAL,
            createdAt = addedAt,
            imagePath = imagePath,
            thumbnailPath = thumbnailPath,
            seed = seed,
            parameters = WallpaperParameters(),
            isFavourite = true
        )
    }
    
    private fun parseCategory(categoryName: String): WallpaperCategory {
        return when (categoryName.uppercase()) {
            "CARTOON" -> WallpaperCategory.SURPRISE
            "AI_COMING_SOON" -> WallpaperCategory.SURPRISE
            else -> try {
                WallpaperCategory.valueOf(categoryName)
            } catch (e: IllegalArgumentException) {
                WallpaperCategory.GRADIENT
            }
        }
    }
    
    private fun Wallpaper.toFavouriteEntity(): FavouriteEntity {
        return FavouriteEntity(
            wallpaperId = id,
            category = category.name,
            isDark = isDark,
            imagePath = imagePath,
            thumbnailPath = thumbnailPath,
            seed = seed,
            parametersJson = "{}",
            addedAt = System.currentTimeMillis()
        )
    }
}
