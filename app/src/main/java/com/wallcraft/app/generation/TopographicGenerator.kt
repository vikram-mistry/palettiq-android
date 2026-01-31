package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Topographic/Contour line generator - map-style elevation patterns
 */
class TopographicGenerator {
    
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
        
        val styleType = random.nextInt(3)
        val baseHue = random.nextFloat() * 360f
        
        when (styleType) {
            0 -> drawContourLines(canvas, width, height, isDark, baseHue, random, paletteColors)
            1 -> drawFilledContours(canvas, width, height, isDark, baseHue, random, paletteColors)
            2 -> drawMinimalLines(canvas, width, height, isDark, baseHue, random, paletteColors)
        }
        
        return bitmap
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try { Color.parseColor(hex) } catch (e: Exception) { null }
    }
    
    private fun drawContourLines(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val bgColor = if (paletteColors.isNotEmpty()) {
            val bgHex = paletteColors[0]
            val baseBg = parseHexColor(bgHex) ?: if (isDark) Color.rgb(12, 12, 18) else Color.rgb(252, 250, 245)
            val hsv = FloatArray(3)
            Color.colorToHSV(baseBg, hsv)
            // Make very dark or very light depending on theme
            hsv[2] = if (isDark) 0.08f else 0.96f
            hsv[1] *= 0.2f 
            Color.HSVToColor(hsv)
        } else {
            if (isDark) Color.rgb(12, 12, 18) else Color.rgb(252, 250, 245)
        }
        canvas.drawColor(bgColor)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        
        // Generate height field using noise
        val heightField = generateHeightField(width, height, random)
        
        val lineColor = if (paletteColors.isNotEmpty()) {
            val colorHex = paletteColors.random(random)
            parseHexColor(colorHex) ?: if (isDark) hslToRgb(hue, 0.4f, 0.5f) else hslToRgb(hue, 0.3f, 0.35f)
        } else {
            if (isDark) hslToRgb(hue, 0.4f, 0.5f) else hslToRgb(hue, 0.3f, 0.35f)
        }
        
        val contourLevels = random.nextInt(15, 25)
        
        for (level in 0 until contourLevels) {
            val threshold = level.toFloat() / contourLevels
            paint.color = lineColor
            paint.alpha = if (level % 5 == 0) 200 else 100
            paint.strokeWidth = if (level % 5 == 0) 2f else 1f
            
            drawContourAtLevel(canvas, heightField, width, height, threshold, paint)
        }
    }
    
    private fun drawFilledContours(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        val heightField = generateHeightField(width, height, random)
        
        val contourLevels = if (paletteColors.isNotEmpty()) paletteColors.size else random.nextInt(8, 15)
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                val heightValue = heightField[y * width + x]
                val level = (heightValue * contourLevels).toInt().coerceIn(0, contourLevels - 1)
                
                if (paletteColors.isNotEmpty()) {
                    val colorHex = paletteColors[level % paletteColors.size]
                    pixels[y * width + x] = parseHexColor(colorHex) ?: Color.BLACK
                } else {
                    val levelHue = (hue + level * 5f) % 360f
                    val saturation = if (isDark) 0.3f + level * 0.03f else 0.25f + level * 0.02f
                    val lightness = if (isDark) {
                        0.1f + level * 0.05f
                    } else {
                        0.9f - level * 0.04f
                    }
                    pixels[y * width + x] = hslToRgb(levelHue, saturation.coerceIn(0f, 1f), lightness.coerceIn(0.1f, 0.9f))
                }
            }
        }
        
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        bitmap.recycle()
        
        // Add contour lines on top
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = if (isDark) Color.argb(60, 255, 255, 255) else Color.argb(40, 0, 0, 0)
        }
        
        for (level in 0 until contourLevels) {
            val threshold = level.toFloat() / contourLevels
            drawContourAtLevel(canvas, heightField, width, height, threshold, paint)
        }
    }
    
    private fun drawMinimalLines(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val bgColor = if (isDark) Color.BLACK else Color.WHITE
        canvas.drawColor(bgColor)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1f
            
            color = if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors.random(random)
                parseHexColor(colorHex) ?: if (isDark) hslToRgb(hue, 0.6f, 0.6f) else hslToRgb(hue, 0.5f, 0.3f)
            } else {
                if (isDark) hslToRgb(hue, 0.6f, 0.6f) else hslToRgb(hue, 0.5f, 0.3f)
            }
        }
        
        // Draw flowing horizontal lines with subtle waves
        val lineCount = random.nextInt(30, 60)
        val amplitude = random.nextFloat() * 30f + 10f
        val frequency = random.nextFloat() * 0.005f + 0.002f
        
        for (i in 0 until lineCount) {
            val baseY = height * (i + 0.5f) / lineCount
            val path = Path()
            
            val phaseOffset = random.nextFloat() * 2 * PI.toFloat()
            val localAmplitude = amplitude * (0.5f + random.nextFloat())
            
            path.moveTo(0f, baseY + sin(phaseOffset) * localAmplitude)
            
            for (x in 0..width step 3) {
                val y = baseY + sin(x * frequency + phaseOffset) * localAmplitude
                path.lineTo(x.toFloat(), y)
            }
            
            paint.alpha = random.nextInt(100, 200)
            canvas.drawPath(path, paint)
        }
    }
    
    private fun generateHeightField(width: Int, height: Int, random: Random): FloatArray {
        val field = FloatArray(width * height)
        val scale = 0.004f + random.nextFloat() * 0.004f
        val octaves = 4
        
        // Create multiple noise centers
        val centerCount = random.nextInt(2, 5)
        val centers = Array(centerCount) {
            Triple(
                random.nextFloat() * width,
                random.nextFloat() * height,
                random.nextFloat() * 0.5f + 0.5f
            )
        }
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                var value = 0f
                
                // Add contribution from each center
                for ((cx, cy, strength) in centers) {
                    val dx = x - cx
                    val dy = y - cy
                    val dist = sqrt(dx * dx + dy * dy)
                    val influence = exp(-dist * 0.003f) * strength
                    value += influence
                }
                
                // Add noise variation
                var noise = 0f
                var amp = 1f
                var freq = scale
                
                for (octave in 0 until octaves) {
                    val nx = x * freq
                    val ny = y * freq
                    noise += sin(nx + sin(ny * 1.5f) * 2f) * amp
                    amp *= 0.5f
                    freq *= 2f
                }
                
                value += noise * 0.3f
                field[y * width + x] = ((value + 1f) / 2f).coerceIn(0f, 1f)
            }
        }
        
        return field
    }
    
    private fun drawContourAtLevel(
        canvas: Canvas,
        heightField: FloatArray,
        width: Int,
        height: Int,
        threshold: Float,
        paint: Paint
    ) {
        // Simplified marching squares for contour detection
        val step = 4
        
        for (y in 0 until height - step step step) {
            for (x in 0 until width - step step step) {
                val v00 = heightField[y * width + x]
                val v10 = heightField[y * width + minOf(x + step, width - 1)]
                val v01 = heightField[minOf(y + step, height - 1) * width + x]
                val v11 = heightField[minOf(y + step, height - 1) * width + minOf(x + step, width - 1)]
                
                // Check if contour passes through this cell
                val above = listOf(v00 > threshold, v10 > threshold, v01 > threshold, v11 > threshold)
                val crossings = above.count { it }
                
                if (crossings > 0 && crossings < 4) {
                    // Draw line segment approximation
                    val points = mutableListOf<Pair<Float, Float>>()
                    
                    // Check edges for crossings
                    if (above[0] != above[1]) {
                        val t = (threshold - v00) / (v10 - v00)
                        points.add(Pair(x + t * step, y.toFloat()))
                    }
                    if (above[1] != above[3]) {
                        val t = (threshold - v10) / (v11 - v10)
                        points.add(Pair((x + step).toFloat(), y + t * step))
                    }
                    if (above[2] != above[3]) {
                        val t = (threshold - v01) / (v11 - v01)
                        points.add(Pair(x + t * step, (y + step).toFloat()))
                    }
                    if (above[0] != above[2]) {
                        val t = (threshold - v00) / (v01 - v00)
                        points.add(Pair(x.toFloat(), y + t * step))
                    }
                    
                    if (points.size >= 2) {
                        canvas.drawLine(points[0].first, points[0].second, points[1].first, points[1].second, paint)
                    }
                }
            }
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
