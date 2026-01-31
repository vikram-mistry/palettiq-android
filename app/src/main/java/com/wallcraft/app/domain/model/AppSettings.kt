package com.wallcraft.app.domain.model

/**
 * Application settings model
 */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val generationFrequency: GenerationFrequency = GenerationFrequency.DAILY,
    val autoChangeEnabled: Boolean = false,
    val autoChangeInterval: AutoChangeInterval = AutoChangeInterval.DAILY,
    val paletteLockEnabled: Boolean = false,
    val lockedPalette: List<String> = emptyList(),
    val lastGenerationDate: Long = 0L,
    val dailyGenerationsCount: Int = 0,
    val showNotifications: Boolean = true
)

enum class ThemeMode(val displayName: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class GenerationFrequency(val displayName: String) {
    DAILY("Daily"),
    MANUAL("Manual")
}

enum class AutoChangeInterval(val displayName: String, val hours: Int) {
    HOURLY("Hourly", 1),
    DAILY("Daily", 24),
    WEEKLY("Weekly", 168)
}
