package com.wallcraft.app.domain.model

/**
 * Core domain model representing a wallpaper
 */
data class Wallpaper(
    val id: String,
    val category: WallpaperCategory,
    val isDark: Boolean,
    val generationType: GenerationType,
    val createdAt: Long,
    val imagePath: String,
    val thumbnailPath: String,
    val seed: Long,
    val parameters: WallpaperParameters,
    val isFavourite: Boolean = false
)

/**
 * Categorization of wallpaper styles
 */
enum class WallpaperCategory(val displayName: String) {
    SURPRISE("Surprise"),  // First - uses CartoonGenerator with random fun patterns
    GRADIENT("Gradient"),
    NOISE("Noise"),
    ABSTRACT("Abstract"),
    AMOLED("AMOLED"),
    GEOMETRIC("Geometric"),
    FLUID("Fluid"),
    TOPOGRAPHIC("Topographic"),
    BOKEH("Bokeh"),
    PASTEL("Pastel")
}

/**
 * Source type of the wallpaper
 */
enum class GenerationType {
    PROCEDURAL,  // Generated on device
    BUNDLED,     // Pre-installed in assets
    DOWNLOADED   // Downloaded from remote
}

/**
 * Pre-defined color palettes for wallpaper generation
 */
enum class ColorPalette(val displayName: String, val colors: List<Int>) {
    RANDOM("Random", emptyList()),
    OCEAN("Ocean", listOf(0xFF0077B6.toInt(), 0xFF00B4D8.toInt(), 0xFF90E0EF.toInt(), 0xFFCAF0F8.toInt(), 0xFF03045E.toInt())),
    SUNSET("Sunset", listOf(0xFFFF6B35.toInt(), 0xFFF7C59F.toInt(), 0xFFEF8354.toInt(), 0xFF2D3142.toInt(), 0xFFFFE66D.toInt())),
    FOREST("Forest", listOf(0xFF2D6A4F.toInt(), 0xFF40916C.toInt(), 0xFF52B788.toInt(), 0xFF74C69D.toInt(), 0xFF95D5B2.toInt())),
    NEON("Neon", listOf(0xFFFF00FF.toInt(), 0xFF00FFFF.toInt(), 0xFFFF0080.toInt(), 0xFF00FF80.toInt(), 0xFF8000FF.toInt())),
    LAVENDER("Lavender", listOf(0xFFE0BBE4.toInt(), 0xFF957DAD.toInt(), 0xFFD291BC.toInt(), 0xFFFEC8D8.toInt(), 0xFFFFDFD3.toInt())),
    MONO_DARK("Monochrome Dark", listOf(0xFF1A1A2E.toInt(), 0xFF16213E.toInt(), 0xFF0F3460.toInt(), 0xFF533483.toInt(), 0xFFE94560.toInt())),
    MONO_LIGHT("Monochrome Light", listOf(0xFFF8F9FA.toInt(), 0xFFE9ECEF.toInt(), 0xFFDEE2E6.toInt(), 0xFFCED4DA.toInt(), 0xFFADB5BD.toInt())),
    CUSTOM("Custom", emptyList())
}

/**
 * Parameters used to generate the wallpaper (for reproducibility)
 */
data class WallpaperParameters(
    val colorPalette: List<String> = emptyList(),
    val patternType: String = "",
    val intensity: Float = 1.0f,
    val scale: Float = 1.0f,
    val complexity: Int = 5,
    val customParams: Map<String, Any> = emptyMap()
)
