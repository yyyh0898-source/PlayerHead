package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ViewLayoutMode
import com.example.ui.components.ActiveFolderSelectorCard
import com.example.ui.components.AppHeader
import com.example.ui.components.ArchiveInspectorSheet
import com.example.ui.components.BatchActionBar
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.CompactAddonRow
import com.example.ui.components.DetailedAddonCard
import com.example.ui.components.FolderSelectorSheet
import com.example.ui.components.GridAddonCard
import com.example.ui.components.SearchAndControlsRow
import com.example.ui.theme.NordicCyan

@Composable
fun AddonVaultScreen(
    viewModel: AddonViewModel,
    onLaunchFilePicker: () -> Unit,
    onLaunchFolderPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val addons by viewModel.filteredAddons.collectAsState()
    val allAddons by viewModel.addons.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val activeFolder by viewModel.activeFolder.collectAsState()
    val layoutMode by viewModel.layoutMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSort by viewModel.selectedSort.collectAsState()
    val isMyanmar by viewModel.isMyanmarLanguage.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val selectedAddonForInspection by viewModel.selectedAddonForInspection.collectAsState()
    val inspectingEntries by viewModel.inspectingEntries.collectAsState()
    val inspectingPackIcons by viewModel.inspectingPackIcons.collectAsState()
    val inspectingFileContent by viewModel.inspectingFileContent.collectAsState()
    val isInspectingLoading by viewModel.isInspectingLoading.collectAsState()
    val folderPickerOpen by viewModel.folderPickerSheetOpen.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val selectedBatchIds by viewModel.selectedBatchIds.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Back handler management
    BackHandler(
        enabled = selectedAddonForInspection != null || folderPickerOpen || selectedBatchIds.isNotEmpty()
    ) {
        when {
            selectedBatchIds.isNotEmpty() -> viewModel.clearBatchSelection()
            selectedAddonForInspection != null -> viewModel.closeInspection()
            folderPickerOpen -> viewModel.closeFolderPicker()
        }
    }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Minimalist Bottom Actions (Import Addon + Pick Folder)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Import Addon Button
                    Button(
                        onClick = onLaunchFilePicker,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("import_addon_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isMyanmar) "အဒ်ဒွန်း အသစ်ထည့်ရန်" else "Import Addon Pack",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Choose Folder Button
                    Surface(
                        onClick = { viewModel.openFolderPicker() },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .weight(0.9f)
                            .height(46.dp)
                            .testTag("quick_folder_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = NordicCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isMyanmar) "ဖိုဒါ ရွေးမည်" else "Select Folder",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // App Header (Brand, Myanmar Language Switcher, Theme Toggle)
            AppHeader(
                isMyanmar = isMyanmar,
                isDarkTheme = isDarkTheme,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onToggleTheme = { viewModel.toggleDarkTheme() },
                batchCount = selectedBatchIds.size,
                onClearBatch = { viewModel.clearBatchSelection() }
            )

            // Active Target Folder Selection Card
            ActiveFolderSelectorCard(
                activeFolder = activeFolder,
                isMyanmar = isMyanmar,
                onOpenFolderPicker = { viewModel.openFolderPicker() }
            )

            // Search Bar & Controls (Layout switcher & Sort)
            SearchAndControlsRow(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                layoutMode = layoutMode,
                onLayoutChange = { viewModel.setLayoutMode(it) },
                selectedSort = selectedSort,
                onSortChange = { viewModel.setSort(it) },
                isMyanmar = isMyanmar
            )

            // Category Filter Pills
            CategoryFilterRow(
                selectedCategory = selectedCategory,
                onCategorySelect = { viewModel.setCategory(it) },
                isMyanmar = isMyanmar
            )

            // Batch Action Bar (if any selected)
            BatchActionBar(
                selectedCount = selectedBatchIds.size,
                isMyanmar = isMyanmar,
                onEnableAll = { viewModel.enableAllSelected() },
                onDisableAll = { viewModel.disableAllSelected() },
                onMoveAll = {
                    activeFolder?.let { viewModel.moveAllSelected(it.nameEn) }
                },
                onDeleteAll = { viewModel.deleteAllSelected() },
                onSelectAll = { viewModel.selectAllFiltered() },
                onClear = { viewModel.clearBatchSelection() }
            )

            // Addon List or Empty State
            if (addons.isEmpty()) {
                EmptyAddonsView(
                    isMyanmar = isMyanmar,
                    onImport = onLaunchFilePicker,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                AnimatedContent(
                    targetState = layoutMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "LayoutTransition",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { mode ->
                    when (mode) {
                        ViewLayoutMode.DETAILED_CARDS -> {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(addons, key = { it.id }) { item ->
                                    DetailedAddonCard(
                                        addon = item,
                                        isMyanmar = isMyanmar,
                                        isSelected = selectedBatchIds.contains(item.id),
                                        availableFolders = folders,
                                        onToggleEnabled = { viewModel.toggleAddonEnabled(item.id) },
                                        onInspect = { viewModel.openInspection(item) },
                                        onRename = { viewModel.renameAddon(item.id, it) },
                                        onMoveFolder = { viewModel.moveAddonToFolder(item.id, it) },
                                        onDelete = { viewModel.deleteAddon(item.id) },
                                        onToggleSelect = { viewModel.toggleBatchSelection(item.id) }
                                    )
                                }
                            }
                        }

                        ViewLayoutMode.COMPACT_LIST -> {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(addons, key = { it.id }) { item ->
                                    CompactAddonRow(
                                        addon = item,
                                        isMyanmar = isMyanmar,
                                        isSelected = selectedBatchIds.contains(item.id),
                                        onToggleEnabled = { viewModel.toggleAddonEnabled(item.id) },
                                        onInspect = { viewModel.openInspection(item) },
                                        onDelete = { viewModel.deleteAddon(item.id) },
                                        onToggleSelect = { viewModel.toggleBatchSelection(item.id) }
                                    )
                                }
                            }
                        }

                        ViewLayoutMode.GRID_MATRIX -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(addons, key = { it.id }) { item ->
                                    GridAddonCard(
                                        addon = item,
                                        isMyanmar = isMyanmar,
                                        isSelected = selectedBatchIds.contains(item.id),
                                        onToggleEnabled = { viewModel.toggleAddonEnabled(item.id) },
                                        onInspect = { viewModel.openInspection(item) },
                                        onToggleSelect = { viewModel.toggleBatchSelection(item.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Folder Selector Sheet
    if (folderPickerOpen) {
        FolderSelectorSheet(
            folders = folders,
            activeFolder = activeFolder,
            isMyanmar = isMyanmar,
            onSelectFolder = { viewModel.selectActiveFolder(it) },
            onPickCustomSafFolder = onLaunchFolderPicker,
            onDismiss = { viewModel.closeFolderPicker() }
        )
    }

    // Archive Inspector Sheet
    selectedAddonForInspection?.let { addon ->
        ArchiveInspectorSheet(
            addon = addon,
            entries = inspectingEntries,
            packIcons = inspectingPackIcons,
            fileContentPair = inspectingFileContent,
            isLoading = isInspectingLoading,
            isMyanmar = isMyanmar,
            onViewEntry = { viewModel.viewEntryContent(addon, it) },
            onCloseEntryContent = { viewModel.closeEntryContent() },
            onDismiss = { viewModel.closeInspection() }
        )
    }
}

@Composable
fun EmptyAddonsView(
    isMyanmar: Boolean,
    onImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inbox,
                    contentDescription = null,
                    tint = NordicCyan,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isMyanmar) "အဒ်ဒွန်းများ မတွေ့ရှိပါ" else "No Addons Found",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isMyanmar) {
                    "ရှာဖွေမှု အမည် မကိုက်ညီပါ သို့မဟုတ် .zip, .mcaddon ဖိုင် အသစ်ထည့်သွင်းပါ"
                } else {
                    "No addons matched your search query or folder filter. Tap below to import a new pack."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onImport,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, NordicCyan)
            ) {
                Text(
                    text = if (isMyanmar) "ဖိုင်ရွေးချယ်ပြီး ထည့်ရန်" else "Import Addon Pack",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
