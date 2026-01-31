package com.wallcraft.app.ui.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wallcraft.app.domain.model.ColorPalette
import com.wallcraft.app.domain.model.WallpaperCategory
import com.wallcraft.app.ui.components.WallpaperGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onWallpaperClick: (String) -> Unit,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCategorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover") },
                actions = {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        WallpaperFilter.values().forEachIndexed { index, filter ->
                            SegmentedButton(
                                selected = uiState.selectedFilter == filter,
                                onClick = { viewModel.setFilter(filter) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = WallpaperFilter.values().size
                                )
                            ) {
                                Text(filter.displayName)
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCategorySheet = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Generate") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.generatingMessage ?: "Loading wallpapers...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Something went wrong",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                
                uiState.wallpapers.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Wallpaper,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No wallpapers yet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap the + button to generate your first wallpaper",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                else -> {
                    WallpaperGrid(
                        wallpapers = uiState.wallpapers,
                        onWallpaperClick = onWallpaperClick,
                        onFavouriteClick = { viewModel.toggleFavourite(it) }
                    )
                }
            }
        }
    }
    
    // Category Selection Bottom Sheet
    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = sheetState
        ) {
            CategorySelectionSheet(
                selectedPalette = uiState.selectedPalette,
                onPaletteSelect = { viewModel.setSelectedPalette(it) },
                onCategorySelect = { category, isDark ->
                    showCategorySheet = false
                    viewModel.generateWallpaper(category, isDark, uiState.selectedPalette)
                },
                onDismiss = { showCategorySheet = false }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategorySelectionSheet(
    selectedPalette: ColorPalette,
    onPaletteSelect: (ColorPalette) -> Unit,
    onCategorySelect: (WallpaperCategory, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var generateDark by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Generate Wallpaper",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Color Palette Selection
        Text(
            text = "Color Palette",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorPalette.values().filter { it != ColorPalette.CUSTOM }.forEach { palette ->
                PaletteChip(
                    palette = palette,
                    isSelected = selectedPalette == palette,
                    onClick = { onPaletteSelect(palette) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Light/Dark toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (generateDark) "Dark Mode" else "Light Mode",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = generateDark,
                onCheckedChange = { generateDark = it }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Style",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Category grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 350.dp)
        ) {
            items(WallpaperCategory.values().toList()) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategorySelect(category, generateDark) }
                )
            }
        }
    }
}

@Composable
private fun PaletteChip(
    palette: ColorPalette,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                ) else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show color swatches for the palette
            if (palette.colors.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    palette.colors.take(4).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                        )
                    }
                }
            } else {
                // Random icon
                Icon(
                    imageVector = Icons.Outlined.Shuffle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = palette.displayName,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: WallpaperCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getCategoryIcon(category: WallpaperCategory): ImageVector {
    return when (category) {
        WallpaperCategory.SURPRISE -> Icons.Outlined.AutoAwesome
        WallpaperCategory.GRADIENT -> Icons.Outlined.Gradient
        WallpaperCategory.NOISE -> Icons.Outlined.Grain
        WallpaperCategory.ABSTRACT -> Icons.Outlined.Brush
        WallpaperCategory.AMOLED -> Icons.Outlined.DarkMode
        WallpaperCategory.GEOMETRIC -> Icons.Outlined.Category
        WallpaperCategory.FLUID -> Icons.Outlined.Water
        WallpaperCategory.TOPOGRAPHIC -> Icons.Outlined.Terrain
        WallpaperCategory.BOKEH -> Icons.Outlined.BlurOn
        WallpaperCategory.PASTEL -> Icons.Outlined.Palette
    }
}
