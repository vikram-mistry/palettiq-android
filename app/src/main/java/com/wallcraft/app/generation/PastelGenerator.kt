package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generator for soft, light pastel color wallpapers
 */
class PastelGenerator {
    
    // Pastel color palettes - soft, muted, high brightness
    private val pastelPalettes = listOf(
        listOf(0xFFFFB3BA.toInt(), 0xFFFFDFBA.toInt(), 0xFFFFFFBA.toInt(), 0xFFBAFFBA.toInt(), 0xFFBAFFFF.toInt()), // Rainbow soft
        listOf(0xFFF8E1F4.toInt(), 0xFFE1F8F8.toInt(), 0xFFF8F8E1.toInt(), 0xFFE1E1F8.toInt()), // Cotton candy
        listOf(0xFFE8D5B7.toInt(), 0xFFF0EAD6.toInt(), 0xFFDDE5ED.toInt(), 0xFFF5E6CC.toInt()), // Cream & latte
        listOf(0xFFB5EAD7.toInt(), 0xFFC7CEEA.toInt(), 0xFFE0BBE4.toInt(), 0xFFFEC8D8.toInt()), // Mint & lavender
        listOf(0xFFFFC8DD.toInt(), 0xFFFFAFCC.toInt(), 0xFFBDE0FE.toInt(), 0xFFA2D2FF.toInt()), // Pink sky
        listOf(0xFFD4E157.toInt(), 0xFFFFF59D.toInt(), 0xFFFFCC80.toInt(), 0xFFFFAB91.toInt()), // Warm sunrise
        listOf(0xFFE1BEE7.toInt(), 0xFFCE93D8.toInt(), 0xFFF3E5F5.toInt(), 0xFFEDE7F6.toInt())  // Lavender dream
    )
    
    fun generate(width: Int, height: Int, seed: Long, isDark: Boolean = false, paletteColors: List<String> = emptyList()): Bitmap {
        val random = Random(seed)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Use custom palette if provided, else use random from defaults
        val palette = if (paletteColors.isNotEmpty()) {
            paletteColors.mapNotNull { parseHexColor(it) }
        } else {
            pastelPalettes[random.nextInt(pastelPalettes.size)]
        }
        
        val style = random.nextInt(5)
        when (style) {
            0 -> drawSoftGradient(canvas, width, height, palette, random)
            1 -> drawCloudPattern(canvas, width, height, palette, random)
            2 -> drawBubbles(canvas, width, height, palette, random)
            3 -> drawWaves(canvas, width, height, palette, random)
            else -> drawSoftBlobs(canvas, width, height, palette, random)
        }
        
        return bitmap
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try { Color.parseColor(hex) } catch (e: Exception) { null }
    }
    
    private fun drawSoftGradient(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Multi-color soft gradient
        val colors = palette.shuffled(random).take(4).toIntArray()
        val positions = floatArrayOf(0f, 0.35f, 0.65f, 1f)
        
        val angle = random.nextFloat() * 360
        val radians = Math.toRadians(angle.toDouble())
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = maxOf(width, height).toFloat()
        
        val x0 = centerX - cos(radians).toFloat() * radius
        val y0 = centerY - sin(radians).toFloat() * radius
        val x1 = centerX + cos(radians).toFloat() * radius
        val y1 = centerY + sin(radians).toFloat() * radius
        
        paint.shader = LinearGradient(x0, y0, x1, y1, colors, positions, Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
    
    private fun drawCloudPattern(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        // Base gradient
        drawSoftGradient(canvas, width, height, palette, random)
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        
        // Draw soft cloud-like blobs
        val numClouds = random.nextInt(5, 12)
        for (i in 0 until numClouds) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val size = random.nextFloat() * width * 0.3f + width * 0.1f
            
            val color = palette[random.nextInt(palette.size)]
            paint.color = color
            paint.alpha = random.nextInt(40, 100)
            paint.maskFilter = BlurMaskFilter(size * 0.5f, BlurMaskFilter.Blur.NORMAL)
            
            canvas.drawCircle(x, y, size, paint)
        }
    }
    
    private fun drawBubbles(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        // Soft background
        val bgColor = palette[0]
        canvas.drawColor(bgColor)
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Draw overlapping pastel circles
        val numBubbles = random.nextInt(15, 30)
        for (i in 0 until numBubbles) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = random.nextFloat() * width * 0.15f + width * 0.05f
            
            val color = palette[random.nextInt(palette.size)]
            paint.color = color
            paint.alpha = random.nextInt(60, 150)
            paint.style = Paint.Style.FILL
            
            canvas.drawCircle(x, y, radius, paint)
            
            // Subtle outline
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            paint.alpha = 40
            canvas.drawCircle(x, y, radius, paint)
        }
    }
    
    private fun drawWaves(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        canvas.drawColor(palette[0])
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        
        val numWaves = palette.size
        for (i in 0 until numWaves) {
            val path = Path()
            val baseY = height * (i + 1) / (numWaves + 1).toFloat()
            val amplitude = height * 0.08f
            
            path.moveTo(0f, height.toFloat())
            path.lineTo(0f, baseY)
            
            var x = 0f
            while (x <= width) {
                val y = baseY + sin(x * 0.01 + random.nextDouble() * PI).toFloat() * amplitude
                path.lineTo(x, y)
                x += 5f
            }
            path.lineTo(width.toFloat(), height.toFloat())
            path.close()
            
            paint.color = palette[i % palette.size]
            paint.alpha = 180
            canvas.drawPath(path, paint)
        }
    }
    
    private fun drawSoftBlobs(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        canvas.drawColor(0xFFFAFAFA.toInt())
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        
        // Large soft background blobs
        for (color in palette) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val size = random.nextFloat() * width * 0.6f + width * 0.2f
            
            paint.color = color
            paint.alpha = 80
            paint.maskFilter = BlurMaskFilter(size * 0.4f, BlurMaskFilter.Blur.NORMAL)
            
            canvas.drawCircle(x, y, size, paint)
        }
    }
}
