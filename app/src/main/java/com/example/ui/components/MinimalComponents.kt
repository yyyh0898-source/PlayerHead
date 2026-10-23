package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ManagedFolder
import com.example.data.model.PackCategory
import com.example.data.model.SortOption
import com.example.data.model.ViewLayoutMode
import com.example.ui.theme.MutedActiveGreen
import com.example.ui.theme.NordicCyan

@Composable
fun AppHeader(
    isMyanmar: Boolean,
    isDarkTheme: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    batchCount: Int,
    onClearBatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(NordicCyan)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMyanmar) "AddonVault" else "AddonVault",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = if (isMyanmar) "အဒ်ဒွန်း စီမံခန့်ခွဲမှုစနစ်" else "Minimalist Game Addon Manager",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Language Switcher Pill
            Surface(
                onClick = onToggleLanguage,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.testTag("language_toggle_button")
            ) {
                Text(
                    text = if (isMyanmar) "MY (မြန်မာ)" else "EN",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // Theme Switcher Button
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .testTag("theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ActiveFolderSelectorCard(
    activeFolder: ManagedFolder?,
    isMyanmar: Boolean,
    onOpenFolderPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onOpenFolderPicker,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("active_folder_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (activeFolder?.isCustomSaf == true) Icons.Default.FolderOpen else Icons.Default.Folder,
                        contentDescription = "Folder",
                        tint = NordicCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isMyanmar) "လက်ရှိ ဖိုဒါ (Active Folder)" else "TARGET DIRECTORY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (activeFolder?.isCustomSaf == true) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NordicCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "SAF",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NordicCyan
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isMyanmar) (activeFolder?.nameMy ?: "ဖိုဒါ ရွေးချယ်ပါ") else (activeFolder?.nameEn ?: "Select Folder"),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = activeFolder?.pathDisplay ?: "No path set",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = if (isMyanmar) "ပြောင်းရန်" else "Change",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun SearchAndControlsRow(
    query: String,
    onQueryChange: (String) -> Unit,
    layoutMode: ViewLayoutMode,
    onLayoutChange: (ViewLayoutMode) -> Unit,
    selectedSort: SortOption,
    onSortChange: (SortOption) -> Unit,
    isMyanmar: Boolean,
    modifier: Modifier = Modifier
) {
    var sortMenuOpen by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Minimalist Search Bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = if (isMyanmar) "အဒ်ဒွန်း အမည်၊ တဂ် ရှာရန်..." else "Search addons, tags, UUID...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(NordicCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("addon_search_input")
                        )
                    }
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Sort Dropdown Button
            Box {
                Surface(
                    onClick = { sortMenuOpen = true },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("sort_menu_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = sortMenuOpen,
                    onDismissRequest = { sortMenuOpen = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    SortOption.values().forEach { sort ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (isMyanmar) sort.titleMy else sort.titleEn,
                                    color = if (selectedSort == sort) NordicCyan else MaterialTheme.colorScheme.primary,
                                    fontWeight = if (selectedSort == sort) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSortChange(sort)
                                sortMenuOpen = false
                            }
                        )
                    }
                }
            }

            // Layout Mode Switcher (Detailed / Compact / Grid)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.height(44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LayoutModeIcon(
                        icon = Icons.Default.ViewAgenda,
                        selected = layoutMode == ViewLayoutMode.DETAILED_CARDS,
                        onClick = { onLayoutChange(ViewLayoutMode.DETAILED_CARDS) },
                        testTag = "layout_detailed_button"
                    )
                    LayoutModeIcon(
                        icon = Icons.Default.ViewHeadline,
                        selected = layoutMode == ViewLayoutMode.COMPACT_LIST,
                        onClick = { onLayoutChange(ViewLayoutMode.COMPACT_LIST) },
                        testTag = "layout_compact_button"
                    )
                    LayoutModeIcon(
                        icon = Icons.Default.GridView,
                        selected = layoutMode == ViewLayoutMode.GRID_MATRIX,
                        onClick = { onLayoutChange(ViewLayoutMode.GRID_MATRIX) },
                        testTag = "layout_grid_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun LayoutModeIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) NordicCyan else MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: PackCategory,
    onCategorySelect: (PackCategory) -> Unit,
    isMyanmar: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PackCategory.values().forEach { category ->
            val isSelected = selectedCategory == category
            Surface(
                onClick = { onCategorySelect(category) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) NordicCyan else MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.testTag("filter_chip_${category.name.lowercase()}")
            ) {
                Text(
                    text = if (isMyanmar) category.titleMy else category.titleEn,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun BatchActionBar(
    selectedCount: Int,
    isMyanmar: Boolean,
    onEnableAll: () -> Unit,
    onDisableAll: () -> Unit,
    onMoveAll: () -> Unit,
    onDeleteAll: () -> Unit,
    onSelectAll: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = selectedCount > 0,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, NordicCyan),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClear, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = if (isMyanmar) "$selectedCount ခု ရွေးထားသည်" else "$selectedCount selected",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onSelectAll, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.SelectAll,
                            contentDescription = "Select All",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onEnableAll, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.ToggleOn,
                            contentDescription = "Enable",
                            tint = MutedActiveGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDisableAll, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.ToggleOff,
                            contentDescription = "Disable",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onMoveAll, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DriveFileMove,
                            contentDescription = "Move",
                            tint = NordicCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDeleteAll, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Delete",
                            tint = Color(0xFFF43F5E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
