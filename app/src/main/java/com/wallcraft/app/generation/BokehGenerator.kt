package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Bokeh/Particles generator - soft light circles, particles, and cosmic patterns
 */
class BokehGenerator {
    
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
        
        val styleType = random.nextInt(4)
        val baseHue = random.nextFloat() * 360f
        
        when (styleType) {
            0 -> drawBokehCircles(canvas, width, height, isDark, baseHue, random, paletteColors)
            1 -> drawParticleTrails(canvas, width, height, isDark, baseHue, random, paletteColors)
            2 -> drawStarfield(canvas, width, height, isDark, baseHue, random, paletteColors)
            3 -> drawCosmicNebula(canvas, width, height, isDark, baseHue, random, paletteColors)
        }
        
        return bitmap
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try { Color.parseColor(hex) } catch (e: Exception) { null }
    }
    
    private fun drawBokehCircles(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        // Gradient background
        val bgColors = if (paletteColors.isNotEmpty()) {
            val bgHex = paletteColors[0]
            val baseBg = parseHexColor(bgHex) ?: if (isDark) hslToRgb(hue, 0.3f, 0.08f) else hslToRgb(hue, 0.2f, 0.9f)
            val hsv = FloatArray(3)
            Color.colorToHSV(baseBg, hsv)
            val color1 = Color.HSVToColor(hsv)
            hsv[2] *= 0.6f // darker
            val color2 = Color.HSVToColor(hsv)
            intArrayOf(color1, color2)
        } else {
            if (isDark) {
                intArrayOf(
                    hslToRgb(hue, 0.3f, 0.08f),
                    hslToRgb((hue + 30f) % 360f, 0.25f, 0.05f)
                )
            } else {
                intArrayOf(
                    hslToRgb(hue, 0.2f, 0.9f),
                    hslToRgb((hue + 30f) % 360f, 0.25f, 0.85f)
                )
            }
        }
        
        val bgGradient = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            bgColors, null, Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply { shader = bgGradient }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        val bokehCount = random.nextInt(30, 70)
        
        for (i in 0 until bokehCount) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = random.nextFloat() * 80f + 20f
            
            val bokehColor = if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors[random.nextInt(paletteColors.size)]
                parseHexColor(colorHex) ?: hslToRgb((hue + random.nextFloat() * 60f - 30f) % 360f, 0.5f, 0.5f)
            } else {
                val bokehHue = (hue + random.nextFloat() * 60f - 30f) % 360f
                val saturation = if (isDark) 0.5f + random.nextFloat() * 0.3f else 0.3f + random.nextFloat() * 0.4f
                val lightness = if (isDark) 0.3f + random.nextFloat() * 0.3f else 0.6f + random.nextFloat() * 0.2f
                hslToRgb(bokehHue, saturation, lightness)
            }
            val gradient = RadialGradient(
                x, y, radius,
                intArrayOf(
                    Color.argb(random.nextInt(30, 80), Color.red(bokehColor), Color.green(bokehColor), Color.blue(bokehColor)),
                    Color.argb(random.nextInt(10, 30), Color.red(bokehColor), Color.green(bokehColor), Color.blue(bokehColor)),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
            
            paint.shader = gradient
            canvas.drawCircle(x, y, radius, paint)
            
            // Add bright edge ring for some bokeh
            if (random.nextFloat() > 0.6f) {
                val ringPaint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                    color = bokehColor
                    alpha = random.nextInt(20, 50)
                }
                canvas.drawCircle(x, y, radius * 0.9f, ringPaint)
            }
        }
    }
    
    private fun drawParticleTrails(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        val bgColor = if (isDark) Color.rgb(5, 5, 10) else Color.rgb(250, 250, 255)
        canvas.drawColor(bgColor)
        
        val paint = Paint().apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
        
        val trailCount = random.nextInt(20, 40)
        
        for (i in 0 until trailCount) {
            val startX = random.nextFloat() * width
            val startY = random.nextFloat() * height
            val length = random.nextFloat() * 200f + 50f
            val angle = random.nextFloat() * 2 * PI.toFloat()
            
            val trailColor = if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors.random(random)
                parseHexColor(colorHex) ?: hslToRgb((hue + random.nextFloat() * 40f) % 360f, 0.7f, if (isDark) 0.5f else 0.4f)
            } else {
                val trailHue = (hue + random.nextFloat() * 40f) % 360f
                hslToRgb(trailHue, 0.7f, if (isDark) 0.5f else 0.4f)
            }
            
            // Draw trail from thick to thin
            val segments = 20
            for (s in 0 until segments) {
                val t = s.toFloat() / segments
                val x1 = startX + cos(angle) * length * t
                val y1 = startY + sin(angle) * length * t
                val x2 = startX + cos(angle) * length * (t + 1f / segments)
                val y2 = startY + sin(angle) * length * (t + 1f / segments)
                
                paint.strokeWidth = (1f - t) * 8f + 1f
                paint.color = trailColor
                paint.alpha = ((1f - t) * 150).toInt() + 20
                
                canvas.drawLine(x1, y1, x2, y2, paint)
            }
            
            // Bright head
            paint.style = Paint.Style.FILL
            paint.color = Color.WHITE
            paint.alpha = 200
            canvas.drawCircle(startX, startY, 3f, paint)
        }
    }
    
    private fun drawStarfield(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        // Deep space gradient
        val gradient = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(
                Color.rgb(5, 5, 15),
                Color.rgb(10, 5, 20),
                Color.rgb(5, 10, 25)
            ),
            null,
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint().apply { shader = gradient }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        // Distant stars (many, small)
        for (i in 0 until 300) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = random.nextFloat() * 1.5f + 0.5f
            val brightness = random.nextFloat()
            
            // Star color temperature variation
            val starHue = when {
                brightness > 0.9f -> 220f // Blue-white
                brightness > 0.7f -> 50f  // Yellow
                brightness > 0.5f -> 30f  // Orange
                else -> 0f                // Red
            }
            
            paint.color = hslToRgb(starHue, 0.3f, 0.7f + brightness * 0.3f)
            paint.alpha = (brightness * 200 + 55).toInt()
            canvas.drawCircle(x, y, radius, paint)
        }
        
        // Medium stars with subtle glow
        for (i in 0 until 50) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = random.nextFloat() * 3f + 2f
            
            // Glow
            val glowGradient = RadialGradient(
                x, y, radius * 4,
                intArrayOf(Color.argb(40, 200, 200, 255), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
            paint.shader = glowGradient
            canvas.drawCircle(x, y, radius * 4, paint)
            paint.shader = null
            
            // Core
            paint.color = Color.WHITE
            paint.alpha = 255
            canvas.drawCircle(x, y, radius, paint)
        }
        
        // Bright stars with flare
        for (i in 0 until 5) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            
            // Cross flare
            paint.strokeWidth = 1.5f
            paint.style = Paint.Style.STROKE
            
            val flareLength = random.nextFloat() * 40f + 30f
            for (angle in listOf(0f, 90f)) {
                val rad = Math.toRadians(angle.toDouble())
                paint.color = Color.argb(80, 255, 255, 255)
                canvas.drawLine(
                    x - cos(rad).toFloat() * flareLength,
                    y - sin(rad).toFloat() * flareLength,
                    x + cos(rad).toFloat() * flareLength,
                    y + sin(rad).toFloat() * flareLength,
                    paint
                )
            }
            
            // Glow
            paint.style = Paint.Style.FILL
            val starGlow = RadialGradient(
                x, y, 20f,
                intArrayOf(Color.WHITE, Color.argb(80, 150, 150, 255), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
            paint.shader = starGlow
            canvas.drawCircle(x, y, 20f, paint)
            paint.shader = null
            
            // Core
            paint.color = Color.WHITE
            canvas.drawCircle(x, y, 3f, paint)
        }
    }
    
    private fun drawCosmicNebula(
        canvas: Canvas,
        width: Int,
        height: Int,
        isDark: Boolean,
        hue: Float,
        random: Random,
        paletteColors: List<String>
    ) {
        canvas.drawColor(Color.rgb(5, 3, 10))
        
        val paint = Paint().apply {
            isAntiAlias = true
        }
        
        // Create nebula clouds with overlapping gradients
        val cloudCount = random.nextInt(5, 10)
        
        for (i in 0 until cloudCount) {
            val cx = random.nextFloat() * width
            val cy = random.nextFloat() * height
            val radius = minOf(width, height) * (0.3f + random.nextFloat() * 0.4f)
            
            val cloudColor = if (paletteColors.isNotEmpty()) {
                val colorHex = paletteColors.random(random)
                parseHexColor(colorHex) ?: hslToRgb((hue + i * 30f + random.nextFloat() * 20f) % 360f, 0.6f, 0.3f)
            } else {
                val cloudHue = (hue + i * 30f + random.nextFloat() * 20f) % 360f
                hslToRgb(cloudHue, 0.6f, 0.3f)
            }
            
            val gradient = RadialGradient(
                cx, cy, radius,
                intArrayOf(
                    Color.argb(60, Color.red(cloudColor), Color.green(cloudColor), Color.blue(cloudColor)),
                    Color.argb(20, Color.red(cloudColor), Color.green(cloudColor), Color.blue(cloudColor)),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            
            paint.shader = gradient
            
            // Stretch for organic shape
            canvas.save()
            canvas.rotate(random.nextFloat() * 360f, cx, cy)
            canvas.scale(1f + random.nextFloat() * 0.5f, 0.5f + random.nextFloat() * 0.5f, cx, cy)
            canvas.drawCircle(cx, cy, radius, paint)
            canvas.restore()
        }
        
        paint.shader = null
        
        // Add stars on top
        for (i in 0 until 150) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val brightness = random.nextFloat()
            
            paint.color = Color.WHITE
            paint.alpha = (brightness * 180 + 50).toInt()
            canvas.drawCircle(x, y, brightness * 1.5f + 0.5f, paint)
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
