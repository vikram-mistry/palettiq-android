package com.wallcraft.app.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wallcraft.app.domain.model.WallpaperCategory
import com.wallcraft.app.domain.repository.FavouritesRepository
import com.wallcraft.app.domain.repository.SettingsRepository
import com.wallcraft.app.generation.WallpaperGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Background worker for automatic wallpaper changes
 */
@HiltWorker
class AutoWallpaperWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val wallpaperGenerator: WallpaperGenerator,
    private val wallpaperApplier: WallpaperApplier,
    private val settingsRepository: SettingsRepository,
    private val favouritesRepository: FavouritesRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val settings = settingsRepository.settings.first()
            
            if (!settings.autoChangeEnabled) {
                return Result.success()
            }
            
            // Try to use a favourite first, otherwise generate new
            val favourites = favouritesRepository.getAllFavourites().first()
            
            val bitmap = if (favourites.isNotEmpty() && kotlin.random.Random.nextFloat() > 0.5f) {
                // Use a random favourite
                val favourite = favourites.random()
                wallpaperGenerator.loadBitmap(favourite)
            } else {
                // Generate a new wallpaper
                val categories = WallpaperCategory.values()
                val category = categories.random()
                val isDark = kotlin.random.Random.nextBoolean()
                val wallpaper = wallpaperGenerator.generate(category, isDark)
                wallpaperGenerator.loadBitmap(wallpaper)
            }
            
            bitmap?.let {
                wallpaperApplier.applyWallpaper(it, WallpaperApplier.WallpaperTarget.BOTH)
                it.recycle()
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
