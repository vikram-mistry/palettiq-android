package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generator for cartoon-style wallpapers with stylized geometric patterns
 * and pastel colors that look playful and cartoon-ish
 */
class CartoonGenerator {
    
    // Bright, playful cartoon palettes
    private val cartoonPalettes = listOf(
        listOf(0xFFFF6B6B.toInt(), 0xFF4ECDC4.toInt(), 0xFFFFE66D.toInt(), 0xFF95E1D3.toInt(), 0xFFF38181.toInt()),
        listOf(0xFFA8E6CF.toInt(), 0xFFDCEDC1.toInt(), 0xFFFFD3B6.toInt(), 0xFFFFAAA5.toInt(), 0xFFFF8B94.toInt()),
        listOf(0xFF6C5CE7.toInt(), 0xFFA29BFE.toInt(), 0xFFFD79A8.toInt(), 0xFFFDCB6E.toInt(), 0xFF00B894.toInt()),
        listOf(0xFFE17055.toInt(), 0xFFFDCB6E.toInt(), 0xFF00CEC9.toInt(), 0xFF0984E3.toInt(), 0xFF6C5CE7.toInt()),
        listOf(0xFFFF9FF3.toInt(), 0xFFFECA57.toInt(), 0xFFFF6B6B.toInt(), 0xFF48DBFB.toInt(), 0xFF1DD1A1.toInt())
    )
    
    fun generate(width: Int, height: Int, seed: Long, isDark: Boolean = false, paletteColors: List<String> = emptyList()): Bitmap {
        val random = Random(seed)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Use custom palette if provided, else use random from defaults
        val palette = if (paletteColors.isNotEmpty()) {
            paletteColors.mapNotNull { parseHexColor(it) }
        } else {
            cartoonPalettes[random.nextInt(cartoonPalettes.size)]
        }
        
        val style = random.nextInt(5)
        when (style) {
            0 -> drawKawaiiPattern(canvas, width, height, palette, random)
            1 -> drawRetroShapes(canvas, width, height, palette, random)
            2 -> drawDoodlePattern(canvas, width, height, palette, random)
            3 -> drawComicDots(canvas, width, height, palette, random)
            else -> drawFunkyStripes(canvas, width, height, palette, random)
        }
        
        return bitmap
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try { Color.parseColor(hex) } catch (e: Exception) { null }
    }
    
    private fun drawKawaiiPattern(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        // Solid pastel background
        canvas.drawColor(palette[random.nextInt(palette.size)])
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        
        // Draw cute rounded shapes
        val shapeSize = width * 0.12f
        val cols = (width / shapeSize).toInt() + 2
        val rows = (height / shapeSize).toInt() + 2
        
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val x = col * shapeSize + (if (row % 2 == 0) 0f else shapeSize / 2)
                val y = row * shapeSize
                
                paint.color = palette[random.nextInt(palette.size)]
                paint.alpha = 200
                
                val shapeType = random.nextInt(4)
                when (shapeType) {
                    0 -> canvas.drawCircle(x, y, shapeSize * 0.35f, paint)
                    1 -> {
                        val rect = RectF(x - shapeSize * 0.3f, y - shapeSize * 0.3f, 
                                        x + shapeSize * 0.3f, y + shapeSize * 0.3f)
                        canvas.drawRoundRect(rect, shapeSize * 0.1f, shapeSize * 0.1f, paint)
                    }
                    2 -> drawStar(canvas, x, y, shapeSize * 0.35f, 5, paint)
                    3 -> drawHeart(canvas, x, y, shapeSize * 0.3f, paint)
                }
            }
        }
    }
    
    private fun drawRetroShapes(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        canvas.drawColor(0xFFFFF8DC.toInt()) // Cream background
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        
        // Large overlapping retro circles and arcs
        for (i in 0 until 12) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val size = random.nextFloat() * width * 0.4f + width * 0.15f
            
            paint.color = palette[random.nextInt(palette.size)]
            
            val shapeType = random.nextInt(3)
            when (shapeType) {
                0 -> canvas.drawCircle(x, y, size, paint)
                1 -> {
                    val rect = RectF(x - size, y - size, x + size, y + size)
                    canvas.drawArc(rect, random.nextFloat() * 180, 180f, true, paint)
                }
                2 -> {
                    val rect = RectF(x - size * 0.5f, y - size, x + size * 0.5f, y + size)
                    canvas.drawOval(rect, paint)
                }
            }
        }
        
        // Add some outline circles for that cartoon look
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        paint.color = 0xFF333333.toInt()
        
        for (i in 0 until 5) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val size = random.nextFloat() * width * 0.2f + width * 0.1f
            canvas.drawCircle(x, y, size, paint)
        }
    }
    
    private fun drawDoodlePattern(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        canvas.drawColor(0xFFFFFBF0.toInt())
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth = 6f
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE
        
        // Draw squiggly lines
        for (i in 0 until 15) {
            paint.color = palette[random.nextInt(palette.size)]
            
            val path = Path()
            var x = random.nextFloat() * width
            var y = random.nextFloat() * height
            path.moveTo(x, y)
            
            for (j in 0 until 8) {
                x += random.nextFloat() * 100 - 50
                y += random.nextFloat() * 100 - 50
                val cx = x + random.nextFloat() * 50 - 25
                val cy = y + random.nextFloat() * 50 - 25
                path.quadTo(cx, cy, x, y)
            }
            
            canvas.drawPath(path, paint)
        }
        
        // Add some filled shapes
        paint.style = Paint.Style.FILL
        for (i in 0 until 20) {
            paint.color = palette[random.nextInt(palette.size)]
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val size = random.nextFloat() * 30 + 10
            
            if (random.nextBoolean()) {
                canvas.drawCircle(x, y, size, paint)
            } else {
                drawStar(canvas, x, y, size, random.nextInt(4) + 4, paint)
            }
        }
    }
    
    private fun drawComicDots(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        // Classic comic book halftone style
        val bgColor = palette[random.nextInt(palette.size)]
        canvas.drawColor(bgColor)
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        
        val dotColor = palette[(palette.indexOf(bgColor) + 1) % palette.size]
        paint.color = dotColor
        
        val dotSize = width * 0.02f
        val spacing = dotSize * 3
        
        var y = 0f
        var row = 0
        while (y < height) {
            var x = if (row % 2 == 0) 0f else spacing / 2
            while (x < width) {
                canvas.drawCircle(x, y, dotSize, paint)
                x += spacing
            }
            y += spacing * 0.866f // Hexagonal packing
            row++
        }
        
        // Add some larger accent circles
        for (i in 0 until 5) {
            paint.color = palette[random.nextInt(palette.size)]
            paint.alpha = 200
            val x = random.nextFloat() * width
            val cY = random.nextFloat() * height
            val size = random.nextFloat() * width * 0.15f + width * 0.05f
            canvas.drawCircle(x, cY, size, paint)
        }
    }
    
    private fun drawFunkyStripes(canvas: Canvas, width: Int, height: Int, palette: List<Int>, random: Random) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        
        val stripeWidth = width / palette.size.toFloat()
        val isWavy = random.nextBoolean()
        
        for ((i, color) in palette.withIndex()) {
            paint.color = color
            
            if (isWavy) {
                val path = Path()
                val startX = i * stripeWidth
                val endX = (i + 1) * stripeWidth
                
                path.moveTo(startX, 0f)
                var y = 0f
                while (y <= height) {
                    val wave = sin(y * 0.02) * 20
                    path.lineTo(startX + wave.toFloat(), y)
                    y += 10
                }
                path.lineTo(endX + sin(height * 0.02).toFloat() * 20, height.toFloat())
                y = height.toFloat()
                while (y >= 0) {
                    val wave = sin(y * 0.02) * 20
                    path.lineTo(endX + wave.toFloat(), y)
                    y -= 10
                }
                path.close()
                
                canvas.drawPath(path, paint)
            } else {
                canvas.drawRect(i * stripeWidth, 0f, (i + 1) * stripeWidth, height.toFloat(), paint)
            }
        }
    }
    
    private fun drawStar(canvas: Canvas, cx: Float, cy: Float, radius: Float, points: Int, paint: Paint) {
        val path = Path()
        val innerRadius = radius * 0.5f
        
        for (i in 0 until points * 2) {
            val r = if (i % 2 == 0) radius else innerRadius
            val angle = Math.PI * i / points - Math.PI / 2
            val x = cx + cos(angle).toFloat() * r
            val y = cy + sin(angle).toFloat() * r
            
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }
    
    private fun drawHeart(canvas: Canvas, cx: Float, cy: Float, size: Float, paint: Paint) {
        val path = Path()
        path.moveTo(cx, cy + size)
        path.cubicTo(cx - size * 1.5f, cy - size * 0.5f, 
                     cx - size * 0.5f, cy - size * 1.5f, 
                     cx, cy - size * 0.5f)
        path.cubicTo(cx + size * 0.5f, cy - size * 1.5f, 
                     cx + size * 1.5f, cy - size * 0.5f, 
                     cx, cy + size)
        canvas.drawPath(path, paint)
    }
}
