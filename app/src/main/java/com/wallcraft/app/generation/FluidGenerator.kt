package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Fluid/Marble pattern generator - creates flowing ink and marble effects
 */
class FluidGenerator {
    
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
            0 -> drawMarbleEffect(canvas, width, height, isDark, baseHue, random, paletteColors)
            1 -> drawFluidGradient(canvas, width, height, isDark, baseHue, random, paletteColors)
            2 -> drawInkBleed(canvas, width, height, isDark, baseHue, random, paletteColors)
        }
        
        return bitmap
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try { Color.parseColor(hex) } catch (e: Exception) { null }
    }
    
    private fun drawMarbleEffect(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val pixels = IntArray(width * height)
        
        val baseColor = if (paletteColors.isNotEmpty()) {
            parseHexColor(paletteColors[0]) ?: if (isDark) hslToRgb(hue, 0.15f, 0.1f) else hslToRgb(hue, 0.1f, 0.95f)
        } else {
            if (isDark) hslToRgb(hue, 0.15f, 0.1f) else hslToRgb(hue, 0.1f, 0.95f)
        }
        
        val veinColor = if (paletteColors.size > 1) {
            parseHexColor(paletteColors[1]) ?: if (isDark) hslToRgb((hue + 30f) % 360f, 0.4f, 0.3f) else hslToRgb((hue + 30f) % 360f, 0.3f, 0.7f)
        } else {
            if (isDark) hslToRgb((hue + 30f) % 360f, 0.4f, 0.3f) else hslToRgb((hue + 30f) % 360f, 0.3f, 0.7f)
        }
        
        val scale = 0.003f + random.nextFloat() * 0.003f
        val turbulence = random.nextFloat() * 5f + 3f
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                // Create marble veins using turbulent noise
                var veinValue = 0f
                var amplitude = 1f
                var frequency = scale
                
                for (octave in 0 until 5) {
                    val nx = x * frequency
                    val ny = y * frequency
                    
                    // Sine-based turbulence for marble effect
                    val noiseX = sin(nx + turbulence * sin(ny * 0.5f))
                    val noiseY = sin(ny + turbulence * sin(nx * 0.5f))
                    
                    veinValue += (noiseX + noiseY) * amplitude
                    amplitude *= 0.5f
                    frequency *= 2f
                }
                
                // Normalize and create marble pattern
                veinValue = sin(x * scale * 2 + veinValue * 3f)
                veinValue = (veinValue + 1f) / 2f
                veinValue = veinValue.pow(0.8f)
                
                pixels[y * width + x] = interpolateColor(baseColor, veinColor, veinValue)
            }
        }
        
        val marbleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        marbleBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        canvas.drawBitmap(marbleBitmap, 0f, 0f, null)
        marbleBitmap.recycle()
    }
    
    private fun drawFluidGradient(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        // Create smooth flowing color bands
        val paint = Paint().apply {
            isAntiAlias = true
        }
        
        // Background
        val bgColor = if (paletteColors.isNotEmpty()) {
            val bgHex = paletteColors[0]
            val baseBg = parseHexColor(bgHex) ?: if (isDark) Color.rgb(5, 5, 10) else Color.rgb(250, 250, 255)
            val hsv = FloatArray(3)
            Color.colorToHSV(baseBg, hsv)
            // Make very dark or very light depending on theme
            hsv[2] = if (isDark) 0.05f else 0.98f
            hsv[1] *= 0.3f 
            Color.HSVToColor(hsv)
        } else {
            if (isDark) Color.rgb(5, 5, 10) else Color.rgb(250, 250, 255)
        }
        canvas.drawColor(bgColor)
        
        val blobCount = random.nextInt(5, 10)
        
        for (i in 0 until blobCount) {
            val blobHue = (hue + i * 25f + random.nextFloat() * 15f) % 360f
            val saturation = if (isDark) 0.5f + random.nextFloat() * 0.3f else 0.4f + random.nextFloat() * 0.4f
            val lightness = if (isDark) 0.2f + random.nextFloat() * 0.2f else 0.5f + random.nextFloat() * 0.3f
            
            val blobColor = if (paletteColors.isNotEmpty()) {
                parseHexColor(paletteColors[random.nextInt(paletteColors.size)]) ?: hslToRgb(blobHue, saturation, lightness)
            } else {
                hslToRgb(blobHue, saturation, lightness)
            }
            
            val centerX = random.nextFloat() * width
            val centerY = random.nextFloat() * height
            val radius = minOf(width, height) * (0.3f + random.nextFloat() * 0.4f)
            
            // Create soft blob with radial gradient
            val gradient = RadialGradient(
                centerX, centerY, radius,
                intArrayOf(
                    blobColor,
                    Color.argb(100, 
                        Color.red(blobColor),
                        Color.green(blobColor),
                        Color.blue(blobColor)
                    ),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            
            paint.shader = gradient
            paint.alpha = random.nextInt(150, 220)
            
            // Draw stretched blob for fluid effect
            canvas.save()
            canvas.rotate(random.nextFloat() * 360f, centerX, centerY)
            canvas.scale(1f + random.nextFloat() * 0.5f, 0.6f + random.nextFloat() * 0.4f, centerX, centerY)
            canvas.drawCircle(centerX, centerY, radius, paint)
            canvas.restore()
        }
    }
    
    private fun drawInkBleed(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val bgColor = if (isDark) Color.rgb(10, 10, 15) else Color.rgb(245, 245, 250)
        canvas.drawColor(bgColor)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        // Create ink bleed spots
        val spotCount = random.nextInt(3, 7)
        
        for (i in 0 until spotCount) {
            val inkHue = (hue + i * 40f) % 360f
            val centerX = random.nextFloat() * width
            val centerY = random.nextFloat() * height
            
            val baseInkColor = if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors[i % paletteColors.size]
                parseHexColor(colorHex) ?: hslToRgb(inkHue, 0.5f, 0.4f)
            } else {
                hslToRgb(inkHue, 0.5f, 0.4f)
            }
            // Get HSL to vary lightness per layer
            val hsv = FloatArray(3)
            Color.colorToHSV(baseInkColor, hsv)
            
            // Draw multiple overlapping irregular circles for ink bleed effect
            val layerCount = random.nextInt(15, 30)
            
            for (layer in 0 until layerCount) {
                val layerRadius = random.nextFloat() * minOf(width, height) * 0.2f + 20f
                val offsetX = random.nextFloat() * 40f - 20f
                val offsetY = random.nextFloat() * 40f - 20f
                
                hsv[2] = if (isDark) 
                    (0.2f + layer * 0.015f).coerceIn(0.1f, 0.8f) // Dark mode: start dark, get lighter? Or inverse? Ink usually dark.
                else 
                    (0.7f - layer * 0.015f).coerceIn(0.2f, 0.9f)
                
                paint.color = Color.HSVToColor(hsv)
                paint.alpha = random.nextInt(30, 80)
                
                // Draw irregular blob
                val path = Path()
                val points = random.nextInt(8, 16)
                
                for (p in 0..points) {
                    val angle = 2 * PI * p / points
                    val radiusVariation = layerRadius * (0.7f + random.nextFloat() * 0.6f)
                    val px = centerX + offsetX + cos(angle).toFloat() * radiusVariation
                    val py = centerY + offsetY + sin(angle).toFloat() * radiusVariation
                    
                    if (p == 0) {
                        path.moveTo(px, py)
                    } else {
                        // Use quadratic curves for smooth edges
                        val prevAngle = 2 * PI * (p - 0.5) / points
                        val cpRadius = layerRadius * (0.8f + random.nextFloat() * 0.4f)
                        val cpX = centerX + offsetX + cos(prevAngle).toFloat() * cpRadius
                        val cpY = centerY + offsetY + sin(prevAngle).toFloat() * cpRadius
                        path.quadTo(cpX, cpY, px, py)
                    }
                }
                path.close()
                
                canvas.drawPath(path, paint)
            }
        }
    }
    
    private fun interpolateColor(color1: Int, color2: Int, fraction: Float): Int {
        val r = (Color.red(color1) + (Color.red(color2) - Color.red(color1)) * fraction).toInt()
        val g = (Color.green(color1) + (Color.green(color2) - Color.green(color1)) * fraction).toInt()
        val b = (Color.blue(color1) + (Color.blue(color2) - Color.blue(color1)) * fraction).toInt()
        return Color.rgb(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255))
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
