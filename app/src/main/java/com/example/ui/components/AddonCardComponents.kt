package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AddonItem
import com.example.data.model.ManagedFolder
import com.example.ui.theme.MutedActiveGreen
import com.example.ui.theme.MutedActiveGreenBg
import com.example.ui.theme.MutedAmber
import com.example.ui.theme.NordicCyan
import java.io.File

@Composable
fun DetailedAddonCard(
    addon: AddonItem,
    isMyanmar: Boolean,
    isSelected: Boolean,
    availableFolders: List<ManagedFolder>,
    onToggleEnabled: () -> Unit,
    onInspect: () -> Unit,
    onRename: (String) -> Unit,
    onMoveFolder: (String) -> Unit,
    onDelete: () -> Unit,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showMoveDialog by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) NordicCyan else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("addon_card_${addon.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Icon, Titles, Badges, and Switch
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Pack Icon or Fallback
                AddonIconBox(
                    addon = addon,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Type Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(addon.packType.accentColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isMyanmar) addon.packType.titleMy else addon.packType.titleEn,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = addon.packType.accentColor
                            )
                        }

                        // Version Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "v${addon.version}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        // Format Badge
                        Text(
                            text = addon.fileFormat.badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = addon.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "By ${addon.author}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Switch Toggle for Enable/Disable
                Column(horizontalAlignment = Alignment.End) {
                    Switch(
                        checked = addon.isEnabled,
                        onCheckedChange = { onToggleEnabled() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MutedActiveGreen,
                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("toggle_switch_${addon.id}")
                    )
                    Text(
                        text = if (addon.isEnabled) (if (isMyanmar) "အသုံးပြုဆဲ" else "ACTIVE") else (if (isMyanmar) "ပိတ်ထားသည်" else "OFF"),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (addon.isEnabled) MutedActiveGreen else MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            if (addon.description.isNotBlank()) {
                Text(
                    text = addon.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Target Folder & Meta Details Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Folder Location Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = NordicCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = addon.targetFolderName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // File Size
                Text(
                    text = addon.formattedSize,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.secondary
                )

                if (addon.fileCount > 0) {
                    Text(
                        text = "• ${addon.fileCount} files",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Inspect Zip Content Button
                Surface(
                    onClick = onInspect,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.testTag("inspect_button_${addon.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = null,
                            tint = NordicCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMyanmar) "ဖိုင်တွင်း စစ်ဆေးရန်" else "Inspect Archive",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Quick Secondary Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Move Folder Button
                    IconButton(
                        onClick = { showMoveDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DriveFileMove,
                            contentDescription = "Move Folder",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isMyanmar) "အမည်ပြောင်းရန်" else "Rename") },
                                onClick = {
                                    showMenu = false
                                    showRenameDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isMyanmar) "ဖိုဒါ ရွှေ့ရန်" else "Move to Folder") },
                                onClick = {
                                    showMenu = false
                                    showMoveDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.DriveFileMove, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (isMyanmar) "ဖျက်ပစ်ရန်" else "Delete Addon",
                                        color = Color(0xFFF43F5E)
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showRenameDialog) {
        RenameDialog(
            currentName = addon.name,
            isMyanmar = isMyanmar,
            onDismiss = { showRenameDialog = false },
            onConfirm = {
                onRename(it)
                showRenameDialog = false
            }
        )
    }

    if (showMoveDialog) {
        MoveFolderDialog(
            currentFolder = addon.targetFolderName,
            availableFolders = availableFolders,
            isMyanmar = isMyanmar,
            onDismiss = { showMoveDialog = false },
            onSelect = {
                onMoveFolder(it)
                showMoveDialog = false
            }
        )
    }
}

@Composable
fun CompactAddonRow(
    addon: AddonItem,
    isMyanmar: Boolean,
    isSelected: Boolean,
    onToggleEnabled: () -> Unit,
    onInspect: () -> Unit,
    onDelete: () -> Unit,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) NordicCyan else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .testTag("compact_addon_row_${addon.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Status Dot + Icon + Title + Version
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Active status dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (addon.isEnabled) MutedActiveGreen else MaterialTheme.colorScheme.secondary)
                )

                Spacer(modifier = Modifier.width(10.dp))

                AddonIconBox(
                    addon = addon,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = addon.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = addon.packType.titleEn,
                            fontSize = 10.sp,
                            color = addon.packType.accentColor
                        )
                        Text(
                            text = "• ${addon.formattedSize}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "• ${addon.targetFolderName}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quick inspect + Switch
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onInspect, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = "Inspect",
                        tint = NordicCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Switch(
                    checked = addon.isEnabled,
                    onCheckedChange = { onToggleEnabled() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MutedActiveGreen,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
fun GridAddonCard(
    addon: AddonItem,
    isMyanmar: Boolean,
    isSelected: Boolean,
    onToggleEnabled: () -> Unit,
    onInspect: () -> Unit,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) NordicCyan else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .testTag("grid_addon_card_${addon.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Icon with active badge overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AddonIconBox(
                    addon = addon,
                    modifier = Modifier.size(64.dp)
                )

                // Top right status indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (addon.isEnabled) MutedActiveGreenBg else MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, if (addon.isEnabled) MutedActiveGreen else MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (addon.isEnabled) "ACTIVE" else "OFF",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (addon.isEnabled) MutedActiveGreen else MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = addon.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = addon.packType.titleEn,
                fontSize = 10.sp,
                color = addon.packType.accentColor,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = addon.formattedSize,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onInspect, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Inspect",
                            tint = NordicCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Switch(
                        checked = addon.isEnabled,
                        onCheckedChange = { onToggleEnabled() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MutedActiveGreen,
                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddonIconBox(
    addon: AddonItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (addon.iconCachePath != null && File(addon.iconCachePath).exists()) {
            AsyncImage(
                model = File(addon.iconCachePath),
                contentDescription = addon.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize().clip(RoundedCornerShape(10.dp))
            )
        } else {
            Icon(
                imageVector = addon.packType.icon,
                contentDescription = null,
                tint = addon.packType.accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun RenameDialog(
    currentName: String,
    isMyanmar: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isMyanmar) "အဒ်ဒွန်း အမည်ပြောင်းရန်" else "Rename Addon")
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (isMyanmar) "အမည်သစ်" else "New Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (isMyanmar) "အတည်ပြုပါ" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isMyanmar) "ပယ်ဖျက်" else "Cancel")
            }
        }
    )
}

@Composable
private fun MoveFolderDialog(
    currentFolder: String,
    availableFolders: List<ManagedFolder>,
    isMyanmar: Boolean,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isMyanmar) "သိုလှောင်မည့် ဖိုဒါ ရွေးချယ်ပါ" else "Move to Target Folder")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableFolders.forEach { folder ->
                    val isCurrent = folder.nameEn == currentFolder
                    Surface(
                        onClick = { onSelect(folder.nameEn) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrent) NordicCyan else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = if (isCurrent) NordicCyan else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isMyanmar) folder.nameMy else folder.nameEn,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = folder.pathDisplay,
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isMyanmar) "ပိတ်ပါ" else "Close")
            }
        }
    )
}
