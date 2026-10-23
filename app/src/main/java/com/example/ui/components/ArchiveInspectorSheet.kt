package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AddonItem
import com.example.data.model.ZipEntryItem
import com.example.ui.theme.NordicCyan
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveInspectorSheet(
    addon: AddonItem,
    entries: List<ZipEntryItem>,
    packIcons: List<String>,
    fileContentPair: Pair<String, String>?,
    isLoading: Boolean,
    isMyanmar: Boolean,
    onViewEntry: (String) -> Unit,
    onCloseEntryContent: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NordicCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderZip,
                            contentDescription = null,
                            tint = NordicCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = addon.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isMyanmar) {
                                "${addon.formattedSize} • ဖိုင်ပေါင်း ${entries.size} ခု ပါဝင်သည်"
                            } else {
                                "${addon.formattedSize} • ${entries.size} files in archive"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tabs: Files Explorer vs Pack Icons Gallery
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NordicCyan
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (isMyanmar) "ဖိုင်များ စာရင်း (${entries.size})" else "Files Tree (${entries.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (isMyanmar) "ပက်ခ် အိုင်ကွန်များ (${packIcons.size})" else "Pack Icons (${packIcons.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NordicCyan, modifier = Modifier.size(36.dp))
                }
            } else if (fileContentPair != null) {
                // Code viewer for tapped JSON or manifest
                FileContentViewer(
                    filePath = fileContentPair.first,
                    content = fileContentPair.second,
                    onBack = onCloseEntryContent,
                    isMyanmar = isMyanmar,
                    modifier = Modifier.weight(1f)
                )
            } else {
                when (selectedTab) {
                    0 -> {
                        // Files List
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(entries, key = { it.path }) { entry ->
                                Surface(
                                    onClick = {
                                        if (entry.isJson || entry.name.endsWith(".txt") || entry.name.endsWith(".html") || entry.name.endsWith(".js")) {
                                            onViewEntry(entry.path)
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = when {
                                                    entry.isDirectory -> Icons.Default.Folder
                                                    entry.isJson -> Icons.Default.Code
                                                    entry.isImage -> Icons.Default.Image
                                                    else -> Icons.Default.Description
                                                },
                                                contentDescription = null,
                                                tint = when {
                                                    entry.isDirectory -> NordicCyan
                                                    entry.isJson -> Color(0xFFFBBF24)
                                                    entry.isImage -> Color(0xFF34D399)
                                                    else -> MaterialTheme.colorScheme.secondary
                                                },
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = entry.path,
                                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                                color = MaterialTheme.colorScheme.primary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        if (!entry.isDirectory) {
                                            Text(
                                                text = entry.formattedSize,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Pack Icons Grid (displays pack_icon0 through pack_icon8 from player heads zip)
                        if (packIcons.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isMyanmar) "ပက်ခ် အိုင်ကွန်များ မတွေ့ရှိပါ" else "No pack icons found in archive",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                items(packIcons) { iconPath ->
                                    val f = File(iconPath)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                        modifier = Modifier.aspectRatio(1f)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(6.dp)
                                        ) {
                                            AsyncImage(
                                                model = f,
                                                contentDescription = f.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = f.name.substringAfterLast('_').substringBefore('.'),
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun FileContentViewer(
    filePath: String,
    content: String,
    onBack: () -> Unit,
    isMyanmar: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = NordicCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = filePath.substringAfterLast('/'),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Surface(
                onClick = onBack,
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = if (isMyanmar) "ပြန်ထွက်မည်" else "Back to List",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
