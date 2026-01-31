package com.wallcraft.app.di

import android.content.Context
import androidx.room.Room
import com.wallcraft.app.data.local.WallpaperDatabase
import com.wallcraft.app.data.local.dao.FavouritesDao
import com.wallcraft.app.data.local.dao.WallpaperDao
import com.wallcraft.app.data.local.datastore.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): WallpaperDatabase {
        return Room.databaseBuilder(
            context,
            WallpaperDatabase::class.java,
            WallpaperDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideFavouritesDao(database: WallpaperDatabase): FavouritesDao {
        return database.favouritesDao()
    }
    
    @Provides
    @Singleton
    fun provideWallpaperDao(database: WallpaperDatabase): WallpaperDao {
        return database.wallpaperDao()
    }
    
    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore {
        return SettingsDataStore(context)
    }
}
