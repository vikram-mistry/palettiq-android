package com.wallcraft.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wallcraft.app.domain.model.Wallpaper

@Composable
fun WallpaperGrid(
    wallpapers: List<Wallpaper>,
    onWallpaperClick: (String) -> Unit,
    onFavouriteClick: (Wallpaper) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = wallpapers,
            key = { it.id }
        ) { wallpaper ->
            WallpaperCard(
                wallpaper = wallpaper,
                onClick = { onWallpaperClick(wallpaper.id) },
                onFavouriteClick = { onFavouriteClick(wallpaper) }
            )
        }
    }
}

@Composable
fun WallpaperGridCompact(
    wallpapers: List<Wallpaper>,
    onWallpaperClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = wallpapers,
            key = { it.id }
        ) { wallpaper ->
            WallpaperCardCompact(
                wallpaper = wallpaper,
                onClick = { onWallpaperClick(wallpaper.id) }
            )
        }
    }
}
