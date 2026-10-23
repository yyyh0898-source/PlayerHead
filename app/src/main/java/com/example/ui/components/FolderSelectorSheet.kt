package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ManagedFolder
import com.example.ui.theme.MutedActiveGreen
import com.example.ui.theme.NordicCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderSelectorSheet(
    folders: List<ManagedFolder>,
    activeFolder: ManagedFolder?,
    isMyanmar: Boolean,
    onSelectFolder: (ManagedFolder) -> Unit,
    onPickCustomSafFolder: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                .padding(20.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NordicCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = NordicCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isMyanmar) "ဖိုဒါ စီမံခန့်ခွဲမှုနှင့် ရွေးချယ်ခြင်း" else "Folder Manager & Selector",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isMyanmar) "အဒ်ဒွန်းများ သိမ်းဆည်းမည့် လမ်းကြောင်း ရွေးပါ" else "Select or link directories on your device",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Button: Pick Folder from Device (SAF)
            Button(
                onClick = onPickCustomSafFolder,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, NordicCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("pick_device_folder_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = null,
                        tint = NordicCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isMyanmar) "စက်တွင်းမှ ဖိုဒါအသစ် ရွေးချယ်ရန် (SAF Folder)" else "Select Folder from Storage (SAF)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Folders List
            Text(
                text = if (isMyanmar) "စီမံထားသော ဖိုဒါများ (MANAGED FOLDERS)" else "MANAGED DIRECTORIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(folders, key = { it.id }) { folder ->
                    val isActive = folder.id == activeFolder?.id

                    Surface(
                        onClick = { onSelectFolder(folder) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isActive) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isActive) 1.5.dp else 1.dp,
                            if (isActive) NordicCyan else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("folder_item_${folder.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isActive) NordicCyan.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (folder.isCustomSaf) Icons.Default.FolderOpen else Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = if (isActive) NordicCyan else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isMyanmar) folder.nameMy else folder.nameEn,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (folder.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "DEFAULT",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = folder.pathDisplay,
                                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontSize = 11.sp
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = if (isMyanmar) {
                                            "${folder.addonCount} ခု သိမ်းဆည်းထားသည် (${String.format("%.1f MB", folder.totalSizeMb)})"
                                        } else {
                                            "${folder.addonCount} addons stored (${String.format("%.1f MB", folder.totalSizeMb)})"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isActive) NordicCyan else MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            if (isActive) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(MutedActiveGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tip note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMyanmar) {
                        "သတိပြုရန်: Android Storage Framework မှတစ်ဆင့် Minecraft သို့မဟုတ် မည်သည့် ဒေါင်းလုဒ် ဖိုဒါကိုမဆို တိုက်ရိုက်ရွေးချယ်ပြီး အဒ်ဒွန်းများကို ထည့်သွင်းစီမံနိုင်ပါသည်။"
                    } else {
                        "Tip: You can select any target folder including Minecraft development folders to install or sync addons directly."
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
