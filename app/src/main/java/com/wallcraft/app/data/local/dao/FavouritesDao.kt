package com.wallcraft.app.data.local.dao

import androidx.room.*
import com.wallcraft.app.data.local.entity.FavouriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for favourites
 */
@Dao
interface FavouritesDao {
    
    @Query("SELECT * FROM favourites ORDER BY addedAt DESC")
    fun getAllFavourites(): Flow<List<FavouriteEntity>>
    
    @Query("SELECT * FROM favourites WHERE isDark = 1 ORDER BY addedAt DESC")
    fun getDarkFavourites(): Flow<List<FavouriteEntity>>
    
    @Query("SELECT * FROM favourites WHERE isDark = 0 ORDER BY addedAt DESC")
    fun getLightFavourites(): Flow<List<FavouriteEntity>>
    
    @Query("SELECT wallpaperId FROM favourites")
    fun getAllFavouriteIds(): Flow<List<String>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE wallpaperId = :wallpaperId)")
    suspend fun isFavourite(wallpaperId: String): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: FavouriteEntity)
    
    @Query("DELETE FROM favourites WHERE wallpaperId = :wallpaperId")
    suspend fun deleteFavourite(wallpaperId: String)
    
    @Query("SELECT COUNT(*) FROM favourites")
    suspend fun getFavouritesCount(): Int
    
    @Query("DELETE FROM favourites")
    suspend fun deleteAllFavourites()
}
