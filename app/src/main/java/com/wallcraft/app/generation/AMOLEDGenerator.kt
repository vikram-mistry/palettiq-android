package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.*
import kotlin.random.Random

/**
 * AMOLED-friendly wallpaper generator optimized for OLED screens
 * Uses true blacks with minimal accent elements for power efficiency
 */
class AMOLEDGenerator {
    
    fun generate(
        width: Int,
        height: Int,
        isDark: Boolean = true, // Always dark for AMOLED
        seed: Long = System.currentTimeMillis(),
        paletteColors: List<String> = emptyList()
    ): Bitmap {
        val random = Random(seed)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // True black background
        canvas.drawColor(Color.BLACK)
        
        val styleType = random.nextInt(5)
        val accentHue = random.nextFloat() * 360f
        
        when (styleType) {
            0 -> drawGlowingDots(canvas, width, height, accentHue, random, paletteColors)
            1 -> drawMinimalLines(canvas, width, height, accentHue, random, paletteColors)
            2 -> drawSubtleGradientCorner(canvas, width, height, accentHue, random, paletteColors)
            3 -> drawStarfield(canvas, width, height, random, paletteColors)
            4 -> drawGeometricAccent(canvas, width, height, accentHue, random, paletteColors)
        }
        
        return bitmap
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try { Color.parseColor(hex) } catch (e: Exception) { null }
    }
    
    private fun drawGlowingDots(
        canvas: Canvas,
        width: Int,
        height: Int,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val dotCount = random.nextInt(20, 50)
        
        for (i in 0 until dotCount) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val baseRadius = random.nextFloat() * 15f + 5f
            
            var dotColor: Int
            
            if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors[random.nextInt(paletteColors.size)]
                dotColor = parseHexColor(colorHex) ?: hslToRgb((hue + random.nextFloat() * 40f - 20f) % 360f, 0.8f, 0.5f)
            } else {
                val dotHue = (hue + random.nextFloat() * 40f - 20f) % 360f
                dotColor = hslToRgb(dotHue, 0.8f, 0.5f)
            }
            
            // Draw glow layers (outer to inner)
            for (layer in 5 downTo 1) {
                val radius = baseRadius * (1f + layer * 0.5f)
                val alpha = (20f / layer).toInt()
                paint.color = dotColor
                paint.alpha = alpha
                canvas.drawCircle(x, y, radius, paint)
            }
            
            // Bright center - make it lighter version of dotColor
            val hsv = FloatArray(3)
            Color.colorToHSV(dotColor, hsv)
            hsv[2] = (hsv[2] * 1.5f).coerceAtMost(1f) // Brighten
            hsv[1] = (hsv[1] * 0.5f).coerceAtMost(1f) // Desaturate
            paint.color = Color.HSVToColor(hsv)
            paint.alpha = 255
            canvas.drawCircle(x, y, baseRadius * 0.3f, paint)
        }
    }
    
    private fun drawMinimalLines(
        canvas: Canvas,
        width: Int,
        height: Int,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        
        val lineCount = random.nextInt(3, 8)
        
        for (i in 0 until lineCount) {
            val startX = random.nextFloat() * width
            val startY = random.nextFloat() * height
            val endX = random.nextFloat() * width
            val endY = random.nextFloat() * height
            
            var lineColor: Int
            
            if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors[i % paletteColors.size]
                lineColor = parseHexColor(colorHex) ?: hslToRgb((hue + i * 15f) % 360f, 0.7f, 0.4f)
            } else {
                val lineHue = (hue + i * 15f) % 360f
                lineColor = hslToRgb(lineHue, 0.7f, 0.4f)
            }
            
            // Draw glow
            for (layer in 4 downTo 1) {
                paint.strokeWidth = layer * 3f + 2f
                paint.color = lineColor
                paint.alpha = (30 / layer)
                canvas.drawLine(startX, startY, endX, endY, paint)
            }
            
            // Core line
            paint.strokeWidth = 2f
            
            // Brighten core
            val hsv = FloatArray(3)
            Color.colorToHSV(lineColor, hsv)
            hsv[2] = (hsv[2] * 1.5f).coerceAtMost(1f) // Brighten
            hsv[1] = (hsv[1] * 0.5f).coerceAtMost(1f) // Desaturate
            
            paint.color = Color.HSVToColor(hsv)
            paint.alpha = 200
            canvas.drawLine(startX, startY, endX, endY, paint)
        }
    }
    
    private fun drawSubtleGradientCorner(
        canvas: Canvas,
        width: Int,
        height: Int,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val cornerType = random.nextInt(4)
        val (centerX, centerY) = when (cornerType) {
            0 -> 0f to 0f
            1 -> width.toFloat() to 0f
            2 -> 0f to height.toFloat()
            else -> width.toFloat() to height.toFloat()
        }
        
        val radius = maxOf(width, height) * 0.8f
        
        val gradientColors = if (paletteColors.isNotEmpty()) {
            val baseColor = parseHexColor(paletteColors[0]) ?: hslToRgb(hue, 0.7f, 0.25f)
            val hsv = FloatArray(3)
            Color.colorToHSV(baseColor, hsv)
            
            val color1 = baseColor
            hsv[2] *= 0.4f // darker
            val color2 = Color.HSVToColor(hsv)
            
            intArrayOf(color1, color2, Color.BLACK)
        } else {
            intArrayOf(
                hslToRgb(hue, 0.7f, 0.25f),
                hslToRgb(hue, 0.5f, 0.1f),
                Color.BLACK
            )
        }
        
        val gradient = RadialGradient(
            centerX, centerY, radius,
            gradientColors,
            floatArrayOf(0f, 0.3f, 0.7f),
            Shader.TileMode.CLAMP
        )
        
        val paint = Paint().apply {
            shader = gradient
            isAntiAlias = true
        }
        
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
    
    private fun drawStarfield(
        canvas: Canvas,
        width: Int,
        height: Int,
        random: Random,
        paletteColors: List<String>
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        // Small distant stars
        val smallStarCount = random.nextInt(100, 200)
        for (i in 0 until smallStarCount) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val brightness = random.nextFloat() * 0.5f + 0.2f
            
            paint.color = Color.WHITE
            paint.alpha = (brightness * 100).toInt()
            canvas.drawCircle(x, y, random.nextFloat() * 1.5f + 0.5f, paint)
        }
        
        // Medium stars with slight glow
        val mediumStarCount = random.nextInt(20, 40)
        for (i in 0 until mediumStarCount) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = random.nextFloat() * 2f + 1f
            
            // Glow
            paint.color = Color.WHITE
            paint.alpha = 30
            canvas.drawCircle(x, y, radius * 3, paint)
            
            // Core
            paint.alpha = (random.nextFloat() * 100 + 155).toInt()
            canvas.drawCircle(x, y, radius, paint)
        }
        
        // Bright stars with cross flare
        val brightStarCount = random.nextInt(3, 8)
        for (i in 0 until brightStarCount) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            
            // Cross flare
            val flareLength = random.nextFloat() * 30f + 20f
            paint.color = Color.WHITE
            paint.alpha = 40
            paint.strokeWidth = 1f
            paint.style = Paint.Style.STROKE
            canvas.drawLine(x - flareLength, y, x + flareLength, y, paint)
            canvas.drawLine(x, y - flareLength, x, y + flareLength, paint)
            
            // Glow
            paint.style = Paint.Style.FILL
            paint.alpha = 50
            canvas.drawCircle(x, y, 8f, paint)
            
            // Core
            paint.alpha = 255
            canvas.drawCircle(x, y, 2f, paint)
        }
    }
    
    private fun drawGeometricAccent(
        canvas: Canvas,
        width: Int,
        height: Int,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        
        val shapeCount = random.nextInt(2, 5)
        val centerX = width / 2f
        val centerY = height / 2f
        
        for (i in 0 until shapeCount) {
            var shapeColor: Int
            
            if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors[i % paletteColors.size]
                shapeColor = parseHexColor(colorHex) ?: hslToRgb((hue + i * 30f) % 360f, 0.7f, 0.4f)
            } else {
                val shapeHue = (hue + i * 30f) % 360f
                shapeColor = hslToRgb(shapeHue, 0.7f, 0.4f)
            }
            
            val sides = random.nextInt(3, 7)
            val radius = minOf(width, height) * (0.15f + i * 0.1f)
            val rotation = random.nextFloat() * 2 * PI.toFloat()
            
            val path = Path()
            for (j in 0..sides) {
                val angle = rotation + 2 * PI.toFloat() * j / sides
                val px = centerX + cos(angle) * radius
                val py = centerY + sin(angle) * radius
                if (j == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()
            
            // Draw with glow
            for (layer in 3 downTo 1) {
                paint.strokeWidth = layer * 2f + 1f
                paint.color = shapeColor
                paint.alpha = (40 / layer)
                canvas.drawPath(path, paint)
            }
            
            // Core line
            paint.strokeWidth = 1.5f
            
            // Brighten core
            val hsv = FloatArray(3)
            Color.colorToHSV(shapeColor, hsv)
            hsv[2] = (hsv[2] * 1.5f).coerceAtMost(1f)
            hsv[1] = (hsv[1] * 0.6f).coerceAtMost(1f)
            
            paint.color = Color.HSVToColor(hsv)
            paint.alpha = 200
            canvas.drawPath(path, paint)
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
