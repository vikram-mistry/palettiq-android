package com.wallcraft.app.generation

import android.graphics.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Noise pattern generator using Perlin and Simplex noise algorithms
 */
class NoiseGenerator {
    
    private val permutation = IntArray(512)
    
    fun generate(
        width: Int,
        height: Int,
        isDark: Boolean,
        seed: Long = System.currentTimeMillis(),
        paletteColors: List<String> = emptyList()
    ): Bitmap {
        val random = Random(seed)
        initPermutation(random)
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        
        val noiseType = random.nextInt(3) // 0: Perlin, 1: Simplex-like, 2: Worley
        
        // Use palette colors if provided, otherwise generate from hue
        val (color1, color2) = if (paletteColors.size >= 2) {
            Pair(parseHexColor(paletteColors[0]) ?: hslToRgb(0f, 0.5f, if (isDark) 0.15f else 0.85f),
                 parseHexColor(paletteColors[1]) ?: hslToRgb(60f, 0.6f, if (isDark) 0.35f else 0.65f))
        } else {
            val baseHue = random.nextFloat() * 360f
            val secondaryHue = (baseHue + 30f + random.nextFloat() * 60f) % 360f
            Pair(hslToRgb(baseHue, if (isDark) 0.5f else 0.7f, if (isDark) 0.15f else 0.85f),
                 hslToRgb(secondaryHue, if (isDark) 0.6f else 0.6f, if (isDark) 0.35f else 0.65f))
        }
        
        val scale = 0.003f + random.nextFloat() * 0.007f
        val octaves = random.nextInt(3, 6)
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                val noiseValue = when (noiseType) {
                    0 -> perlinNoise(x * scale, y * scale, octaves)
                    1 -> simplexLike(x * scale, y * scale, octaves)
                    else -> worleyNoise(x * scale * 3, y * scale * 3, random)
                }
                
                val normalized = ((noiseValue + 1f) / 2f).coerceIn(0f, 1f)
                pixels[y * width + x] = interpolateColor(color1, color2, normalized)
            }
        }
        
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
    
    private fun parseHexColor(hex: String): Int? {
        return try { Color.parseColor(hex) } catch (e: Exception) { null }
    }
    
    private fun initPermutation(random: Random) {
        val p = (0 until 256).shuffled(random)
        for (i in 0 until 256) {
            permutation[i] = p[i]
            permutation[256 + i] = p[i]
        }
    }
    
    private fun perlinNoise(x: Float, y: Float, octaves: Int): Float {
        var total = 0f
        var frequency = 1f
        var amplitude = 1f
        var maxValue = 0f
        
        for (i in 0 until octaves) {
            total += noise2D(x * frequency, y * frequency) * amplitude
            maxValue += amplitude
            amplitude *= 0.5f
            frequency *= 2f
        }
        
        return total / maxValue
    }
    
    private fun noise2D(x: Float, y: Float): Float {
        val xi = x.toInt() and 255
        val yi = y.toInt() and 255
        val xf = x - x.toInt()
        val yf = y - y.toInt()
        
        val u = fade(xf)
        val v = fade(yf)
        
        val aa = permutation[permutation[xi] + yi]
        val ab = permutation[permutation[xi] + yi + 1]
        val ba = permutation[permutation[xi + 1] + yi]
        val bb = permutation[permutation[xi + 1] + yi + 1]
        
        val x1 = lerp(grad(aa, xf, yf), grad(ba, xf - 1, yf), u)
        val x2 = lerp(grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1), u)
        
        return lerp(x1, x2, v)
    }
    
    private fun simplexLike(x: Float, y: Float, octaves: Int): Float {
        var total = 0f
        var frequency = 1f
        var amplitude = 1f
        var maxValue = 0f
        
        for (i in 0 until octaves) {
            val nx = x * frequency
            val ny = y * frequency
            
            // Simple 2D noise approximation
            val value = sin(nx * 12.9898f + ny * 78.233f) * 43758.5453123f
            total += (value - value.toInt()) * 2f - 1f
            total *= amplitude
            
            maxValue += amplitude
            amplitude *= 0.5f
            frequency *= 2f
        }
        
        return (total / maxValue).coerceIn(-1f, 1f)
    }
    
    private fun worleyNoise(x: Float, y: Float, random: Random): Float {
        val cellX = x.toInt()
        val cellY = y.toInt()
        
        var minDist = Float.MAX_VALUE
        
        for (dx in -1..1) {
            for (dy in -1..1) {
                val neighborX = cellX + dx
                val neighborY = cellY + dy
                
                // Deterministic random point in cell
                val pointX = neighborX + hash(neighborX, neighborY, 0)
                val pointY = neighborY + hash(neighborX, neighborY, 1)
                
                val dist = sqrt((x - pointX).pow(2) + (y - pointY).pow(2))
                minDist = minOf(minDist, dist)
            }
        }
        
        return (minDist * 2f - 1f).coerceIn(-1f, 1f)
    }
    
    private fun hash(x: Int, y: Int, seed: Int): Float {
        var n = x + y * 57 + seed * 131
        n = n shl 13 xor n
        return ((n * (n * n * 15731 + 789221) + 1376312589) and 0x7fffffff) / 2147483647f
    }
    
    private fun fade(t: Float): Float = t * t * t * (t * (t * 6 - 15) + 10)
    
    private fun lerp(a: Float, b: Float, t: Float): Float = a + t * (b - a)
    
    private fun grad(hash: Int, x: Float, y: Float): Float {
        return when (hash and 3) {
            0 -> x + y
            1 -> -x + y
            2 -> x - y
            else -> -x - y
        }
    }
    
    private fun interpolateColor(color1: Int, color2: Int, fraction: Float): Int {
        val r = (Color.red(color1) + (Color.red(color2) - Color.red(color1)) * fraction).toInt()
        val g = (Color.green(color1) + (Color.green(color2) - Color.green(color1)) * fraction).toInt()
        val b = (Color.blue(color1) + (Color.blue(color2) - Color.blue(color1)) * fraction).toInt()
        return Color.rgb(r, g, b)
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
