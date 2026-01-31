package com.wallcraft.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wallcraft.app.domain.model.AutoChangeInterval
import com.wallcraft.app.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showIntervalDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(title = { Text("Settings") })
        
        // Appearance Section
        SettingsSection(title = "Appearance") {
            SettingsItem(
                icon = Icons.Outlined.Palette,
                title = "Theme",
                subtitle = uiState.settings.themeMode.displayName,
                onClick = { showThemeDialog = true }
            )
        }
        
        // Automation Section
        SettingsSection(title = "Automation") {
            SettingsSwitch(
                icon = Icons.Outlined.Schedule,
                title = "Auto-change wallpaper",
                subtitle = "Automatically change wallpaper on schedule",
                checked = uiState.settings.autoChangeEnabled,
                onCheckedChange = { viewModel.setAutoChangeEnabled(it) }
            )
            
            if (uiState.settings.autoChangeEnabled) {
                SettingsItem(
                    icon = Icons.Outlined.Timer,
                    title = "Change interval",
                    subtitle = uiState.settings.autoChangeInterval.displayName,
                    onClick = { showIntervalDialog = true }
                )
            }
        }
        
        // Notifications Section
        SettingsSection(title = "Notifications") {
            SettingsSwitch(
                icon = Icons.Outlined.Notifications,
                title = "Show notifications",
                subtitle = "Notify when wallpaper changes",
                checked = uiState.settings.showNotifications,
                onCheckedChange = { viewModel.setShowNotifications(it) }
            )
        }
        
        // Storage Section
        SettingsSection(title = "Storage") {
            SettingsItem(
                icon = Icons.Outlined.Storage,
                title = "Clear cache",
                subtitle = "Cache size: ${uiState.cacheSize}",
                onClick = { viewModel.clearCache() },
                enabled = !uiState.isClearing
            )
        }
        
        // About Section
        SettingsSection(title = "About") {
            SettingsItem(
                icon = Icons.Outlined.Info,
                title = "Version",
                subtitle = "1.0.0",
                onClick = { }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
    
    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose theme") },
            text = {
                Column {
                    ThemeMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.settings.themeMode == mode,
                                onClick = {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(mode.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Interval Dialog
    if (showIntervalDialog) {
        AlertDialog(
            onDismissRequest = { showIntervalDialog = false },
            title = { Text("Change interval") },
            text = {
                Column {
                    AutoChangeInterval.values().forEach { interval ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setAutoChangeInterval(interval)
                                    showIntervalDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.settings.autoChangeInterval == interval,
                                onClick = {
                                    viewModel.setAutoChangeInterval(interval)
                                    showIntervalDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(interval.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIntervalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null)
        },
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick)
    )
}

@Composable
private fun SettingsSwitch(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null)
        },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    )
}
