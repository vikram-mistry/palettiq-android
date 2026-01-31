package com.wallcraft.app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Boot receiver to schedule auto wallpaper change after device restart
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule wallpaper change work after boot
            scheduleWallpaperChange(context)
        }
    }
    
    companion object {
        private const val WORK_NAME = "auto_wallpaper_change"
        
        fun scheduleWallpaperChange(context: Context, intervalHours: Int = 24) {
            val workRequest = PeriodicWorkRequestBuilder<AutoWallpaperWorker>(
                intervalHours.toLong(),
                TimeUnit.HOURS
            ).build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
        
        fun cancelWallpaperChange(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
