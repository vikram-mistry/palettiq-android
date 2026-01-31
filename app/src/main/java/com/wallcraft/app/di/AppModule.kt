package com.wallcraft.app.di

import android.content.Context
import com.wallcraft.app.generation.*
import com.wallcraft.app.util.WallpaperApplier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideWallpaperGenerator(
        @ApplicationContext context: Context
    ): WallpaperGenerator {
        return WallpaperGenerator(context)
    }
    
    @Provides
    @Singleton
    fun provideGradientGenerator(): GradientGenerator {
        return GradientGenerator()
    }
    
    @Provides
    @Singleton
    fun provideNoiseGenerator(): NoiseGenerator {
        return NoiseGenerator()
    }
    
    @Provides
    @Singleton
    fun provideShapeGenerator(): ShapeGenerator {
        return ShapeGenerator()
    }
    
    @Provides
    @Singleton
    fun provideAmoledGenerator(): AMOLEDGenerator {
        return AMOLEDGenerator()
    }
    
    @Provides
    @Singleton
    fun provideGeometricGenerator(): GeometricGenerator {
        return GeometricGenerator()
    }
    
    @Provides
    @Singleton
    fun provideFluidGenerator(): FluidGenerator {
        return FluidGenerator()
    }
    
    @Provides
    @Singleton
    fun provideTopographicGenerator(): TopographicGenerator {
        return TopographicGenerator()
    }
    
    @Provides
    @Singleton
    fun provideBokehGenerator(): BokehGenerator {
        return BokehGenerator()
    }
    
    @Provides
    @Singleton
    fun provideWallpaperApplier(
        @ApplicationContext context: Context
    ): WallpaperApplier {
        return WallpaperApplier(context)
    }
    
    @Provides
    @Singleton
    fun providePastelGenerator(): PastelGenerator {
        return PastelGenerator()
    }
    
    @Provides
    @Singleton
    fun provideCartoonGenerator(): CartoonGenerator {
        return CartoonGenerator()
    }
}
