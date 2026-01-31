package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Gradient wallpaper generator - creates beautiful gradient backgrounds
 * Supports linear, radial, sweep, and mesh gradients
 */
class GradientGenerator {
    
    /**
     * Generate a gradient wallpaper
     * @param paletteColors Optional list of hex color strings (e.g., "#FF5733") to use instead of random colors
     */
    fun generate(
        width: Int,
        height: Int,
        isDark: Boolean,
        seed: Long = System.currentTimeMillis(),
        paletteColors: List<String> = emptyList()
    ): Bitmap {
        val random = Random(seed)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val colors = if (paletteColors.isNotEmpty()) {
            paletteColors.mapNotNull { parseHexColor(it) }.toIntArray()
        } else {
            generateColorPalette(isDark, random)
        }
        
        val gradientType = random.nextInt(4)
        
        when (gradientType) {
            0 -> drawLinearGradient(canvas, width, height, colors, random)
            1 -> drawRadialGradient(canvas, width, height, colors, random)
            2 -> drawSweepGradient(canvas, width, height, colors, random)
            3 -> drawMeshGradient(canvas, width, height, colors, random)
        }
        
        // Add subtle noise overlay for texture
        addNoiseOverlay(canvas, width, height, random)
        
        return bitmap
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try {
            Color.parseColor(hex)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun generateColorPalette(isDark: Boolean, random: Random): IntArray {
        val baseHue = random.nextFloat() * 360f
        val colors = mutableListOf<Int>()
        
        // Generate 3-5 harmonious colors
        val colorCount = random.nextInt(3, 6)
        for (i in 0 until colorCount) {
            val hue = (baseHue + i * (360f / colorCount) + random.nextFloat() * 30f) % 360f
            val saturation = if (isDark) random.nextFloat() * 0.4f + 0.3f else random.nextFloat() * 0.5f + 0.4f
            val lightness = if (isDark) random.nextFloat() * 0.3f + 0.1f else random.nextFloat() * 0.3f + 0.5f
            colors.add(hslToRgb(hue, saturation, lightness))
        }
        
        return colors.toIntArray()
    }
    
    private fun drawLinearGradient(
        canvas: Canvas,
        width: Int,
        height: Int,
        colors: IntArray,
        random: Random
    ) {
        val angle = random.nextFloat() * 360f
        val radians = Math.toRadians(angle.toDouble())
        
        val centerX = width / 2f
        val centerY = height / 2f
        val diagonal = sqrt((width * width + height * height).toFloat())
        
        val startX = centerX - cos(radians).toFloat() * diagonal / 2
        val startY = centerY - sin(radians).toFloat() * diagonal / 2
        val endX = centerX + cos(radians).toFloat() * diagonal / 2
        val endY = centerY + sin(radians).toFloat() * diagonal / 2
        
        val gradient = LinearGradient(
            startX, startY, endX, endY,
            colors,
            null,
            Shader.TileMode.CLAMP
        )
        
        val paint = Paint().apply {
            shader = gradient
            isAntiAlias = true
        }
        
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
    
    private fun drawRadialGradient(
        canvas: Canvas,
        width: Int,
        height: Int,
        colors: IntArray,
        random: Random
    ) {
        val centerX = width * (0.2f + random.nextFloat() * 0.6f)
        val centerY = height * (0.2f + random.nextFloat() * 0.6f)
        val radius = maxOf(width, height) * (0.8f + random.nextFloat() * 0.4f)
        
        val gradient = RadialGradient(
            centerX, centerY, radius,
            colors,
            null,
            Shader.TileMode.CLAMP
        )
        
        val paint = Paint().apply {
            shader = gradient
            isAntiAlias = true
        }
        
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
    
    private fun drawSweepGradient(
        canvas: Canvas,
        width: Int,
        height: Int,
        colors: IntArray,
        random: Random
    ) {
        val centerX = width * (0.3f + random.nextFloat() * 0.4f)
        val centerY = height * (0.3f + random.nextFloat() * 0.4f)
        
        val gradient = SweepGradient(centerX, centerY, colors, null)
        
        val paint = Paint().apply {
            shader = gradient
            isAntiAlias = true
        }
        
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
    
    private fun drawMeshGradient(
        canvas: Canvas,
        width: Int,
        height: Int,
        colors: IntArray,
        random: Random
    ) {
        // Draw multiple overlapping radial gradients for mesh effect
        val pointCount = random.nextInt(3, 6)
        
        // Start with a base color
        canvas.drawColor(colors.first())
        
        for (i in 0 until pointCount) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = minOf(width, height) * (0.3f + random.nextFloat() * 0.5f)
            
            val colorIndex = i % colors.size
            val gradientColors = intArrayOf(
                colors[colorIndex],
                Color.argb(0, Color.red(colors[colorIndex]), Color.green(colors[colorIndex]), Color.blue(colors[colorIndex]))
            )
            
            val gradient = RadialGradient(
                x, y, radius,
                gradientColors,
                null,
                Shader.TileMode.CLAMP
            )
            
            val paint = Paint().apply {
                shader = gradient
                isAntiAlias = true
                alpha = 180
            }
            
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }
    
    private fun addNoiseOverlay(canvas: Canvas, width: Int, height: Int, random: Random) {
        val paint = Paint().apply {
            isAntiAlias = false
        }
        
        // Subtle noise for texture
        for (i in 0 until (width * height / 50)) {
            val x = random.nextInt(width)
            val y = random.nextInt(height)
            val alpha = random.nextInt(5, 15)
            paint.color = Color.argb(alpha, 255, 255, 255)
            canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
        }
    }
    
    private fun hslToRgb(h: Float, s: Float, l: Float): Int {
        val c = (1f - abs(2f * l - 1f)) * s
        val x = c * (1f - abs((h / 60f) % 2f - 1f))
        val m = l - c / 2f
        
        val (r1, g1, b1) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        
        return Color.rgb(
            ((r1 + m) * 255).toInt().coerceIn(0, 255),
            ((g1 + m) * 255).toInt().coerceIn(0, 255),
            ((b1 + m) * 255).toInt().coerceIn(0, 255)
        )
    }
}
