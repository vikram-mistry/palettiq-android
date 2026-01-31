package com.wallcraft.app.generation

import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.view.WindowManager
import com.wallcraft.app.domain.model.GenerationType
import com.wallcraft.app.domain.model.Wallpaper
import com.wallcraft.app.domain.model.WallpaperCategory
import com.wallcraft.app.domain.model.WallpaperParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Main wallpaper generator that coordinates all generation algorithms
 * and handles storage of generated wallpapers
 */
@Singleton
class WallpaperGenerator @Inject constructor(
    private val context: Context
) {
    
    private val gradientGenerator = GradientGenerator()
    private val noiseGenerator = NoiseGenerator()
    private val shapeGenerator = ShapeGenerator()
    private val amoledGenerator = AMOLEDGenerator()
    private val geometricGenerator = GeometricGenerator()
    private val fluidGenerator = FluidGenerator()
    private val topographicGenerator = TopographicGenerator()
    private val bokehGenerator = BokehGenerator()
    private val pastelGenerator = PastelGenerator()
    private val cartoonGenerator = CartoonGenerator()
    
    private val wallpaperDir: File by lazy {
        File(context.filesDir, "wallpapers").also { it.mkdirs() }
    }
    
    private val thumbnailDir: File by lazy {
        File(context.filesDir, "thumbnails").also { it.mkdirs() }
    }
    
    /**
     * Generate a single wallpaper of the specified category
     */
    suspend fun generate(
        category: WallpaperCategory,
        isDark: Boolean,
        seed: Long = System.currentTimeMillis(),
        lockedColors: List<String> = emptyList()
    ): Wallpaper = withContext(Dispatchers.Default) {
        val (width, height) = getScreenDimensions()
        
        val bitmap = when (category) {
            WallpaperCategory.SURPRISE -> cartoonGenerator.generate(width, height, seed, isDark, lockedColors)
            WallpaperCategory.GRADIENT -> gradientGenerator.generate(width, height, isDark, seed, lockedColors)
            WallpaperCategory.NOISE -> noiseGenerator.generate(width, height, isDark, seed, lockedColors)
            WallpaperCategory.ABSTRACT -> shapeGenerator.generate(width, height, isDark, seed, lockedColors)
            WallpaperCategory.AMOLED -> amoledGenerator.generate(width, height, true, seed, lockedColors)
            WallpaperCategory.GEOMETRIC -> geometricGenerator.generate(width, height, isDark, seed, lockedColors)
            WallpaperCategory.FLUID -> fluidGenerator.generate(width, height, isDark, seed, lockedColors)
            WallpaperCategory.TOPOGRAPHIC -> topographicGenerator.generate(width, height, isDark, seed, lockedColors)
            WallpaperCategory.BOKEH -> bokehGenerator.generate(width, height, isDark, seed, lockedColors)
            WallpaperCategory.PASTEL -> pastelGenerator.generate(width, height, seed, isDark, lockedColors)
        }
        
        val id = UUID.randomUUID().toString()
        val (imagePath, thumbnailPath) = saveBitmap(bitmap, id)
        
        Wallpaper(
            id = id,
            category = category,
            isDark = isDark || category == WallpaperCategory.AMOLED,
            generationType = GenerationType.PROCEDURAL,
            createdAt = System.currentTimeMillis(),
            imagePath = imagePath,
            thumbnailPath = thumbnailPath,
            seed = seed,
            parameters = WallpaperParameters(
                colorPalette = lockedColors,
                patternType = category.name
            )
        )
    }
    
    /**
     * Generate a batch of wallpapers with variety
     */
    suspend fun generateBatch(
        count: Int,
        preferDark: Boolean = false
    ): List<Wallpaper> = withContext(Dispatchers.Default) {
        val wallpapers = mutableListOf<Wallpaper>()
        val categories = WallpaperCategory.values().toList()
        
        for (i in 0 until count) {
            val category = categories[i % categories.size]
            val isDark = if (category == WallpaperCategory.AMOLED) true else preferDark
            val seed = System.currentTimeMillis() + i * 1000
            
            wallpapers.add(generate(category, isDark, seed))
        }
        
        wallpapers
    }
    
    /**
     * Regenerate a wallpaper with a new seed (variation)
     */
    suspend fun regenerate(wallpaper: Wallpaper): Wallpaper {
        // Delete old files
        deleteWallpaperFiles(wallpaper)
        
        // Generate new with different seed
        return generate(
            category = wallpaper.category,
            isDark = wallpaper.isDark,
            seed = System.currentTimeMillis()
        )
    }
    
    /**
     * Load a wallpaper bitmap from storage
     */
    suspend fun loadBitmap(wallpaper: Wallpaper): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = File(wallpaper.imagePath)
            if (file.exists()) {
                android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Load a thumbnail bitmap
     */
    suspend fun loadThumbnail(wallpaper: Wallpaper): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = File(wallpaper.thumbnailPath)
            if (file.exists()) {
                android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Delete wallpaper files
     */
    fun deleteWallpaperFiles(wallpaper: Wallpaper) {
        File(wallpaper.imagePath).delete()
        File(wallpaper.thumbnailPath).delete()
    }
    
    /**
     * Clear all generated wallpapers
     */
    suspend fun clearCache() = withContext(Dispatchers.IO) {
        wallpaperDir.listFiles()?.forEach { it.delete() }
        thumbnailDir.listFiles()?.forEach { it.delete() }
    }
    
    /**
     * Get cache size in bytes
     */
    fun getCacheSize(): Long {
        val wallpaperSize = wallpaperDir.listFiles()?.sumOf { it.length() } ?: 0L
        val thumbnailSize = thumbnailDir.listFiles()?.sumOf { it.length() } ?: 0L
        return wallpaperSize + thumbnailSize
    }
    
    private fun getScreenDimensions(): Pair<Int, Int> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(metrics)
        return Pair(metrics.widthPixels, metrics.heightPixels)
    }
    
    private suspend fun saveBitmap(bitmap: Bitmap, id: String): Pair<String, String> = withContext(Dispatchers.IO) {
        val imageFile = File(wallpaperDir, "$id.jpg")
        val thumbFile = File(thumbnailDir, "${id}_thumb.jpg")
        
        // Save full resolution
        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
        
        // Create and save thumbnail
        val thumbWidth = 400
        val thumbHeight = (bitmap.height * thumbWidth / bitmap.width.toFloat()).toInt()
        val thumbnail = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, true)
        
        FileOutputStream(thumbFile).use { out ->
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        
        thumbnail.recycle()
        
        Pair(imageFile.absolutePath, thumbFile.absolutePath)
    }
}
