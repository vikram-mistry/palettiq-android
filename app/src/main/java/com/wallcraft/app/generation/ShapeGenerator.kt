package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Abstract shape generator - creates compositions of geometric shapes and curves
 */
class ShapeGenerator {
    
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
        
        // Draw background
        val bgColor = if (isDark) {
            hslToRgb(random.nextFloat() * 360f, 0.15f, 0.08f)
        } else {
            hslToRgb(random.nextFloat() * 360f, 0.1f, 0.95f)
        }
        canvas.drawColor(bgColor)
        
        val baseHue = random.nextFloat() * 360f
        val shapeType = random.nextInt(4)
        
        if (paletteColors.isNotEmpty()) {
            // Use first color for background if palette exists
            val bgHex = paletteColors[0]
            val bgInt = parseHexColor(bgHex) ?: bgColor
            // Darken it a bit for background
            val hsv = FloatArray(3)
            Color.colorToHSV(bgInt, hsv)
            hsv[2] = (hsv[2] * 0.2f).coerceAtMost(1f)
            canvas.drawColor(Color.HSVToColor(hsv))
        }

        when (shapeType) {
            0 -> drawBezierComposition(canvas, width, height, isDark, baseHue, random, paletteColors)
            1 -> drawGeometricOverlay(canvas, width, height, isDark, baseHue, random, paletteColors)
            2 -> drawWavePatterns(canvas, width, height, isDark, baseHue, random, paletteColors)
            3 -> drawCircleComposition(canvas, width, height, isDark, baseHue, random, paletteColors)
        }
        
        return bitmap
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try { Color.parseColor(hex) } catch (e: Exception) { null }
    }
    
    private fun drawBezierComposition(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        baseHue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val curveCount = random.nextInt(3, 7)
        
        for (i in 0 until curveCount) {
            val path = Path()
            val hue = (baseHue + i * 30f) % 360f
            val saturation = if (isDark) 0.5f + random.nextFloat() * 0.3f else 0.4f + random.nextFloat() * 0.4f
            val lightness = if (isDark) 0.2f + random.nextFloat() * 0.3f else 0.5f + random.nextFloat() * 0.3f
            
            if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors[random.nextInt(paletteColors.size)]
                paint.color = parseHexColor(colorHex) ?: hslToRgb(hue, saturation, lightness)
            } else {
                paint.color = hslToRgb(hue, saturation, lightness)
            }
            paint.alpha = random.nextInt(100, 200)
            
            // Create flowing bezier curve
            val startY = random.nextFloat() * height
            path.moveTo(-width * 0.1f, startY)
            
            val controlPoints = random.nextInt(2, 4)
            var currentX = 0f
            
            for (j in 0 until controlPoints) {
                val cp1x = currentX + width / controlPoints * 0.3f
                val cp1y = random.nextFloat() * height
                val cp2x = currentX + width / controlPoints * 0.7f
                val cp2y = random.nextFloat() * height
                val endX = currentX + width / controlPoints
                val endY = random.nextFloat() * height
                
                path.cubicTo(cp1x, cp1y, cp2x, cp2y, endX, endY)
                currentX = endX
            }
            
            // Close the path to create a filled shape
            path.lineTo(width * 1.1f, height * 1.1f)
            path.lineTo(-width * 0.1f, height * 1.1f)
            path.close()
            
            canvas.drawPath(path, paint)
        }
    }
    
    private fun drawGeometricOverlay(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        baseHue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
        }
        
        val shapeCount = random.nextInt(10, 25)
        
        for (i in 0 until shapeCount) {
            val hue = (baseHue + random.nextFloat() * 60f) % 360f
            val saturation = if (isDark) 0.4f + random.nextFloat() * 0.4f else 0.3f + random.nextFloat() * 0.5f
            val lightness = if (isDark) 0.15f + random.nextFloat() * 0.35f else 0.4f + random.nextFloat() * 0.4f
            
            if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors[random.nextInt(paletteColors.size)]
                paint.color = parseHexColor(colorHex) ?: hslToRgb(hue, saturation, lightness)
            } else {
                paint.color = hslToRgb(hue, saturation, lightness)
            }
            paint.alpha = random.nextInt(50, 150)
            paint.style = if (random.nextBoolean()) Paint.Style.FILL else Paint.Style.STROKE
            paint.strokeWidth = random.nextFloat() * 8f + 2f
            
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val size = random.nextFloat() * minOf(width, height) * 0.3f + 20f
            
            when (random.nextInt(3)) {
                0 -> canvas.drawCircle(x, y, size / 2, paint)
                1 -> {
                    val path = Path()
                    val sides = random.nextInt(3, 7)
                    for (j in 0..sides) {
                        val angle = 2 * PI * j / sides
                        val px = x + cos(angle).toFloat() * size / 2
                        val py = y + sin(angle).toFloat() * size / 2
                        if (j == 0) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                    path.close()
                    canvas.drawPath(path, paint)
                }
                2 -> canvas.drawRect(x - size / 2, y - size / 2, x + size / 2, y + size / 2, paint)
            }
        }
    }
    
    private fun drawWavePatterns(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        baseHue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val waveCount = random.nextInt(5, 10)
        
        for (i in 0 until waveCount) {
            val path = Path()
            val hue = (baseHue + i * 20f) % 360f
            val saturation = if (isDark) 0.5f else 0.6f
            val lightness = if (isDark) 0.15f + i * 0.05f else 0.8f - i * 0.05f
            
            if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors[i % paletteColors.size]
                // Make waves progressively lighter or darker based on index for variety
                val baseColor = parseHexColor(colorHex) ?: hslToRgb(hue, saturation, lightness)
                paint.color = baseColor
            } else {
                paint.color = hslToRgb(hue, saturation, lightness.coerceIn(0.1f, 0.9f))
            }
            paint.alpha = 200
            
            val baseY = height * (i + 1f) / (waveCount + 1f)
            val amplitude = random.nextFloat() * 50f + 30f
            val frequency = random.nextFloat() * 0.02f + 0.005f
            val phase = random.nextFloat() * 2 * PI.toFloat()
            
            path.moveTo(0f, height.toFloat())
            path.lineTo(0f, baseY + sin(phase) * amplitude)
            
            for (x in 0..width step 5) {
                val y = baseY + sin(x * frequency + phase) * amplitude
                path.lineTo(x.toFloat(), y)
            }
            
            path.lineTo(width.toFloat(), height.toFloat())
            path.close()
            
            canvas.drawPath(path, paint)
        }
    }
    
    private fun drawCircleComposition(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        baseHue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val circleCount = random.nextInt(15, 35)
        
        for (i in 0 until circleCount) {
            val hue = (baseHue + random.nextFloat() * 90f) % 360f
            val saturation = if (isDark) 0.4f + random.nextFloat() * 0.4f else 0.3f + random.nextFloat() * 0.5f
            val lightness = if (isDark) 0.15f + random.nextFloat() * 0.25f else 0.5f + random.nextFloat() * 0.35f
            
            if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors[random.nextInt(paletteColors.size)]
                paint.color = parseHexColor(colorHex) ?: hslToRgb(hue, saturation, lightness)
            } else {
                paint.color = hslToRgb(hue, saturation, lightness)
            }
            paint.alpha = random.nextInt(30, 120)
            
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = random.nextFloat() * minOf(width, height) * 0.25f + 30f
            
            canvas.drawCircle(x, y, radius, paint)
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
