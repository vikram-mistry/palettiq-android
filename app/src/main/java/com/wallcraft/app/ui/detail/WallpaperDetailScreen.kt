package com.wallcraft.app.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.wallcraft.app.util.WallpaperApplier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperDetailScreen(
    wallpaperId: String,
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showApplyDialog by remember { mutableStateOf(false) }
    
    // Handle snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Wallpaper preview
            uiState.wallpaper?.let { wallpaper ->
                AsyncImage(
                    model = wallpaper.imagePath,
                    contentDescription = "Wallpaper preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Top bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                
                FilledTonalIconButton(onClick = { viewModel.toggleFavourite() }) {
                    Icon(
                        imageVector = if (uiState.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (uiState.isFavourite) "Remove from favourites" else "Add to favourites",
                        tint = if (uiState.isFavourite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Bottom action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Download button
                FilledTonalButton(
                    onClick = { viewModel.downloadWallpaper() },
                    enabled = !uiState.isDownloading,
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }
                
                // Apply button
                Button(
                    onClick = { showApplyDialog = true },
                    enabled = !uiState.isApplying,
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isApplying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            Icons.Filled.Wallpaper,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply")
                }
            }
        }
    }
    
    // Apply Dialog
    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = { Text("Apply wallpaper") },
            text = { Text("Where would you like to apply this wallpaper?") },
            confirmButton = {
                Column {
                    TextButton(
                        onClick = {
                            viewModel.applyWallpaper(WallpaperApplier.WallpaperTarget.HOME_SCREEN)
                            showApplyDialog = false
                        }
                    ) {
                        Text("Home screen")
                    }
                    TextButton(
                        onClick = {
                            viewModel.applyWallpaper(WallpaperApplier.WallpaperTarget.LOCK_SCREEN)
                            showApplyDialog = false
                        }
                    ) {
                        Text("Lock screen")
                    }
                    TextButton(
                        onClick = {
                            viewModel.applyWallpaper(WallpaperApplier.WallpaperTarget.BOTH)
                            showApplyDialog = false
                        }
                    ) {
                        Text("Both")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showApplyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
