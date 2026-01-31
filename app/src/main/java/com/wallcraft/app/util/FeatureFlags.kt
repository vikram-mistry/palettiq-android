package com.wallcraft.app.util

/**
 * Feature flags for controlling premium features and monetization hooks
 * Currently all disabled - architecture prepared for future implementation
 */
object FeatureFlags {
    
    // Premium tier controls
    const val PREMIUM_ENABLED = false
    const val MAX_DAILY_GENERATIONS_FREE = 10
    const val UNLIMITED_GENERATIONS_PREMIUM = true
    
    // Feature toggles
    const val AD_FREE_ENABLED = false
    const val CLOUD_SYNC_ENABLED = false
    const val EXCLUSIVE_PACKS_ENABLED = false
    
    // Remote config keys (for future Firebase Remote Config integration)
    const val KEY_PREMIUM_ENABLED = "premium_enabled"
    const val KEY_DAILY_LIMIT = "daily_generation_limit"
    const val KEY_SHOW_ADS = "show_ads"
    
    /**
     * Check if user has premium access
     * TODO: Implement with billing library
     */
    fun isPremium(): Boolean {
        return false // Will be connected to billing library
    }
    
    /**
     * Check if user can generate more wallpapers today
     */
    fun canGenerate(currentCount: Int): Boolean {
        return if (isPremium() && UNLIMITED_GENERATIONS_PREMIUM) {
            true
        } else {
            currentCount < MAX_DAILY_GENERATIONS_FREE
        }
    }
    
    /**
     * Get remaining generations for today
     */
    fun getRemainingGenerations(currentCount: Int): Int {
        return if (isPremium() && UNLIMITED_GENERATIONS_PREMIUM) {
            Int.MAX_VALUE
        } else {
            (MAX_DAILY_GENERATIONS_FREE - currentCount).coerceAtLeast(0)
        }
    }
    
    /**
     * Check if a specific wallpaper pack is accessible
     */
    fun hasAccessToPack(packId: String, isPremiumPack: Boolean): Boolean {
        return if (isPremiumPack) {
            isPremium()
        } else {
            true
        }
    }
}

/**
 * Entitlement checker interface for future billing integration
 */
interface EntitlementChecker {
    fun isPremium(): Boolean
    fun getDailyGenerationsRemaining(): Int
    fun hasAccessToPack(packId: String): Boolean
    fun hasPurchased(productId: String): Boolean
}

/**
 * Default implementation that treats all users as free tier
 */
class DefaultEntitlementChecker : EntitlementChecker {
    override fun isPremium() = false
    override fun getDailyGenerationsRemaining() = FeatureFlags.MAX_DAILY_GENERATIONS_FREE
    override fun hasAccessToPack(packId: String) = true
    override fun hasPurchased(productId: String) = false
}
