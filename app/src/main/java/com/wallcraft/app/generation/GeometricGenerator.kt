package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Geometric pattern generator - tessellations, op-art, and isometric patterns
 */
class GeometricGenerator {
    
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
        
        val patternType = random.nextInt(4)
        
        // Use first palette color for base hue if available
        val baseHue = if (paletteColors.isNotEmpty()) {
            extractHueFromHex(paletteColors[0]) ?: random.nextFloat() * 360f
        } else {
            random.nextFloat() * 360f
        }
        
        when (patternType) {
            0 -> drawHexagonalTessellation(canvas, width, height, isDark, baseHue, random, paletteColors)
            1 -> drawTriangleTessellation(canvas, width, height, isDark, baseHue, random, paletteColors)
            2 -> drawIsometricCubes(canvas, width, height, isDark, baseHue, random, paletteColors)
            3 -> drawOpArtPattern(canvas, width, height, isDark, baseHue, random, paletteColors)
        }
        
        return bitmap
    }
    
    private fun extractHueFromHex(hex: String): Float? {
        return try {
            val color = Color.parseColor(hex)
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            hsv[0]
        } catch (e: Exception) { null }
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try { Color.parseColor(hex) } catch (e: Exception) { null }
    }
    
    private fun drawHexagonalTessellation(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val bgColor = if (isDark) Color.rgb(15, 15, 20) else Color.rgb(245, 245, 250)
        canvas.drawColor(bgColor)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val hexSize = random.nextFloat() * 40f + 40f
        val hexWidth = hexSize * 2
        val hexHeight = sqrt(3f) * hexSize
        
        var row = 0
        var y = -hexHeight
        while (y < height + hexHeight) {
            var x = if (row % 2 == 0) 0f else hexSize * 1.5f
            x -= hexWidth
            
            while (x < width + hexWidth) {
                val colorVariation = random.nextFloat() * 30f - 15f
                val hexHue = (hue + colorVariation) % 360f
                val saturation = if (isDark) 0.4f + random.nextFloat() * 0.3f else 0.3f + random.nextFloat() * 0.4f
                val lightness = if (isDark) 0.15f + random.nextFloat() * 0.2f else 0.6f + random.nextFloat() * 0.25f
                
                if (paletteColors.isNotEmpty()) {
                    val colorHex = paletteColors[random.nextInt(paletteColors.size)]
                    paint.color = parseHexColor(colorHex) ?: hslToRgb(hexHue, saturation, lightness)
                } else {
                    paint.color = hslToRgb(hexHue, saturation, lightness)
                }
                
                drawHexagon(canvas, x, y, hexSize, paint)
                x += hexSize * 3
            }
            y += hexHeight / 2
            row++
        }
        
        // Draw outlines
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = if (isDark) Color.argb(40, 255, 255, 255) else Color.argb(40, 0, 0, 0)
        
        row = 0
        y = -hexHeight
        while (y < height + hexHeight) {
            var x = if (row % 2 == 0) 0f else hexSize * 1.5f
            x -= hexWidth
            
            while (x < width + hexWidth) {
                drawHexagon(canvas, x, y, hexSize, paint)
                x += hexSize * 3
            }
            y += hexHeight / 2
            row++
        }
    }
    
    private fun drawHexagon(canvas: Canvas, cx: Float, cy: Float, size: Float, paint: Paint) {
        val path = Path()
        for (i in 0..6) {
            val angle = PI.toFloat() / 3 * i - PI.toFloat() / 6
            val x = cx + cos(angle) * size
            val y = cy + sin(angle) * size
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }
    
    private fun drawTriangleTessellation(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val bgColor = if (isDark) Color.rgb(10, 10, 15) else Color.rgb(250, 250, 255)
        canvas.drawColor(bgColor)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val triSize = random.nextFloat() * 60f + 60f
        val triHeight = triSize * sqrt(3f) / 2
        
        var row = 0
        var y = -triHeight
        while (y < height + triHeight) {
            var x = -triSize
            while (x < width + triSize) {
                val pointing = (row + (x / triSize).toInt()) % 2 == 0
                
                val colorVariation = random.nextFloat() * 40f - 20f
                val triHue = (hue + colorVariation) % 360f
                val saturation = if (isDark) 0.5f + random.nextFloat() * 0.3f else 0.4f + random.nextFloat() * 0.4f
                val lightness = if (isDark) 0.12f + random.nextFloat() * 0.18f else 0.55f + random.nextFloat() * 0.3f
                
                if (paletteColors.isNotEmpty()) {
                    val colorHex = paletteColors[random.nextInt(paletteColors.size)]
                    paint.color = parseHexColor(colorHex) ?: hslToRgb(triHue, saturation, lightness)
                } else {
                    paint.color = hslToRgb(triHue, saturation, lightness)
                }
                
                drawTriangle(canvas, x, y, triSize, triHeight, pointing, paint)
                x += triSize / 2
            }
            y += triHeight
            row++
        }
    }
    
    private fun drawTriangle(
        canvas: Canvas,
        x: Float,
        y: Float,
        size: Float,
        height: Float,
        pointingUp: Boolean,
        paint: Paint
    ) {
        val path = Path()
        if (pointingUp) {
            path.moveTo(x, y + height)
            path.lineTo(x + size / 2, y)
            path.lineTo(x + size, y + height)
        } else {
            path.moveTo(x, y)
            path.lineTo(x + size / 2, y + height)
            path.lineTo(x + size, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }
    
    private fun drawIsometricCubes(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val bgColor = if (isDark) Color.rgb(8, 8, 12) else Color.rgb(248, 248, 252)
        canvas.drawColor(bgColor)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val cubeSize = random.nextFloat() * 40f + 50f
        val cubeWidth = cubeSize * 2
        val cubeHeight = cubeSize * sqrt(3f)
        
        var row = 0
        var y = -cubeHeight
        while (y < height + cubeHeight * 2) {
            var x = if (row % 2 == 0) -cubeWidth else -cubeWidth / 2
            
            while (x < width + cubeWidth) {
                val cubeHue = (hue + random.nextFloat() * 30f) % 360f
                drawIsometricCube(canvas, x, y, cubeSize, isDark, cubeHue, paint, random, paletteColors)
                x += cubeWidth
            }
            y += cubeHeight * 0.75f
            row++
        }
    }
    
    private fun drawIsometricCube(
        canvas: Canvas,
        x: Float,
        y: Float,
        size: Float,
        isDark: Boolean,
        hue: Float,
        paint: Paint,
        random: Random,
        paletteColors: List<String>
    ) {
        val halfW = size
        val quarterH = size * sqrt(3f) / 4
        
        // Top face
        val topPath = Path().apply {
            moveTo(x, y - quarterH * 2)
            lineTo(x + halfW, y - quarterH)
            lineTo(x, y)
            lineTo(x - halfW, y - quarterH)
            close()
        }
        
        if (paletteColors.isNotEmpty()) {
            // Use palette colors with different brightness/shades for faces
            val baseColor = parseHexColor(paletteColors[random.nextInt(paletteColors.size)]) ?: Color.GRAY
            val hsv = FloatArray(3)
            Color.colorToHSV(baseColor, hsv)
            
            // Top face (lightest)
            hsv[2] = (hsv[2] * 1.2f).coerceAtMost(1f)
            paint.color = Color.HSVToColor(hsv)
            canvas.drawPath(topPath, paint)
            
            // Left face (medium)
            val leftPath = Path().apply {
                moveTo(x - halfW, y - quarterH)
                lineTo(x, y)
                lineTo(x, y + quarterH * 2)
                lineTo(x - halfW, y + quarterH)
                close()
            }
            hsv[2] = (hsv[2] * 0.7f).coerceAtMost(1f)
            paint.color = Color.HSVToColor(hsv)
            canvas.drawPath(leftPath, paint)
            
            // Right face (darkest)
            val rightPath = Path().apply {
                moveTo(x + halfW, y - quarterH)
                lineTo(x, y)
                lineTo(x, y + quarterH * 2)
                lineTo(x + halfW, y + quarterH)
                close()
            }
            hsv[2] = (hsv[2] * 0.8f).coerceAtMost(1f) // Relative to previous dark
            paint.color = Color.HSVToColor(hsv)
            canvas.drawPath(rightPath, paint)
            
        } else {
            paint.color = hslToRgb(hue, if (isDark) 0.5f else 0.6f, if (isDark) 0.35f else 0.75f)
            canvas.drawPath(topPath, paint)
            
            // Left face
            val leftPath = Path().apply {
                moveTo(x - halfW, y - quarterH)
                lineTo(x, y)
                lineTo(x, y + quarterH * 2)
                lineTo(x - halfW, y + quarterH)
                close()
            }
            paint.color = hslToRgb(hue, if (isDark) 0.5f else 0.6f, if (isDark) 0.2f else 0.55f)
            canvas.drawPath(leftPath, paint)
            
            // Right face
            val rightPath = Path().apply {
                moveTo(x + halfW, y - quarterH)
                lineTo(x, y)
                lineTo(x, y + quarterH * 2)
                lineTo(x + halfW, y + quarterH)
                close()
            }
            paint.color = hslToRgb(hue, if (isDark) 0.5f else 0.6f, if (isDark) 0.25f else 0.65f)
            canvas.drawPath(rightPath, paint)
        }
    }
    
    private fun drawOpArtPattern(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val color1 = if (isDark) Color.BLACK else Color.WHITE
        val color2 = if (paletteColors.isNotEmpty()) {
            parseHexColor(paletteColors[random.nextInt(paletteColors.size)]) ?: hslToRgb(hue, 0.7f, if (isDark) 0.3f else 0.5f)
        } else {
            hslToRgb(hue, 0.7f, if (isDark) 0.3f else 0.5f)
        }
        
        canvas.drawColor(color1)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = color2
        }
        
        val patternVariant = random.nextInt(2)
        
        when (patternVariant) {
            0 -> {
                // Concentric circles
                val cx = width / 2f
                val cy = height / 2f
                val maxRadius = maxOf(width, height).toFloat()
                val ringWidth = random.nextFloat() * 20f + 15f
                
                var r = maxRadius
                var alternate = true
                while (r > 0) {
                    paint.color = if (alternate) color2 else color1
                    canvas.drawCircle(cx, cy, r, paint)
                    r -= ringWidth
                    alternate = !alternate
                }
            }
            1 -> {
                // Wavy lines
                val lineCount = random.nextInt(15, 30)
                val amplitude = random.nextFloat() * 30f + 20f
                val frequency = random.nextFloat() * 0.02f + 0.01f
                
                for (i in 0 until lineCount) {
                    val baseY = height * i.toFloat() / lineCount
                    val path = Path()
                    
                    path.moveTo(0f, baseY)
                    for (x in 0..width step 5) {
                        val y = baseY + sin(x * frequency + i * 0.5f) * amplitude
                        path.lineTo(x.toFloat(), y)
                    }
                    path.lineTo(width.toFloat(), baseY + height / lineCount)
                    for (x in width downTo 0 step 5) {
                        val y = baseY + height / lineCount + sin(x * frequency + i * 0.5f) * amplitude
                        path.lineTo(x.toFloat(), y)
                    }
                    path.close()
                    
                    paint.color = if (i % 2 == 0) color2 else color1
                    canvas.drawPath(path, paint)
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
