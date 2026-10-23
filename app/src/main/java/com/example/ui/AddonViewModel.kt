package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AddonItem
import com.example.data.model.ManagedFolder
import com.example.data.model.PackCategory
import com.example.data.model.PackType
import com.example.data.model.SortOption
import com.example.data.model.ViewLayoutMode
import com.example.data.model.ZipEntryItem
import com.example.data.repository.AddonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AddonRepository(application)

    private val _addons = MutableStateFlow<List<AddonItem>>(emptyList())
    val addons: StateFlow<List<AddonItem>> = _addons.asStateFlow()

    private val _folders = MutableStateFlow<List<ManagedFolder>>(emptyList())
    val folders: StateFlow<List<ManagedFolder>> = _folders.asStateFlow()

    private val _activeFolder = MutableStateFlow<ManagedFolder?>(null)
    val activeFolder: StateFlow<ManagedFolder?> = _activeFolder.asStateFlow()

    private val _layoutMode = MutableStateFlow(ViewLayoutMode.DETAILED_CARDS)
    val layoutMode: StateFlow<ViewLayoutMode> = _layoutMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(PackCategory.ALL)
    val selectedCategory: StateFlow<PackCategory> = _selectedCategory.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortOption.RECENT)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    private val _isMyanmarLanguage = MutableStateFlow(true)
    val isMyanmarLanguage: StateFlow<Boolean> = _isMyanmarLanguage.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _selectedAddonForInspection = MutableStateFlow<AddonItem?>(null)
    val selectedAddonForInspection: StateFlow<AddonItem?> = _selectedAddonForInspection.asStateFlow()

    private val _inspectingEntries = MutableStateFlow<List<ZipEntryItem>>(emptyList())
    val inspectingEntries: StateFlow<List<ZipEntryItem>> = _inspectingEntries.asStateFlow()

    private val _inspectingPackIcons = MutableStateFlow<List<String>>(emptyList())
    val inspectingPackIcons: StateFlow<List<String>> = _inspectingPackIcons.asStateFlow()

    private val _inspectingFileContent = MutableStateFlow<Pair<String, String>?>(null)
    val inspectingFileContent: StateFlow<Pair<String, String>?> = _inspectingFileContent.asStateFlow()

    private val _isInspectingLoading = MutableStateFlow(false)
    val isInspectingLoading: StateFlow<Boolean> = _isInspectingLoading.asStateFlow()

    private val _folderPickerSheetOpen = MutableStateFlow(false)
    val folderPickerSheetOpen: StateFlow<Boolean> = _folderPickerSheetOpen.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _selectedBatchIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedBatchIds: StateFlow<Set<String>> = _selectedBatchIds.asStateFlow()

    val filteredAddons: StateFlow<List<AddonItem>> = combine(
        _addons,
        _searchQuery,
        _selectedCategory,
        _selectedSort,
        _activeFolder
    ) { addonList, query, category, sort, folder ->
        var result = addonList

        // Filter by folder if specific folder active
        if (folder != null && !folder.isDefault) {
            result = result.filter { it.targetFolderName == folder.nameEn }
        }

        // Filter by category
        result = when (category) {
            PackCategory.ALL -> result
            PackCategory.ACTIVE -> result.filter { it.isEnabled }
            PackCategory.BEHAVIOR -> result.filter { it.packType == PackType.BEHAVIOR_PACK }
            PackCategory.RESOURCE -> result.filter { it.packType == PackType.RESOURCE_PACK }
            PackCategory.HEADS -> result.filter { it.packType == PackType.PLAYER_HEADS || it.packType == PackType.SKIN_PACK }
            PackCategory.ARCHIVES -> result.filter { it.packType == PackType.ARCHIVE }
        }

        // Filter by search query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.name.lowercase().contains(q) ||
                    it.description.lowercase().contains(q) ||
                    it.author.lowercase().contains(q) ||
                    it.tags.any { tag -> tag.lowercase().contains(q) } ||
                    it.fileFormat.extension.lowercase().contains(q) ||
                    it.uuid.lowercase().contains(q)
            }
        }

        // Sort
        when (sort) {
            SortOption.RECENT -> result.sortedByDescending { it.dateAdded }
            SortOption.NAME -> result.sortedBy { it.name.lowercase() }
            SortOption.SIZE -> result.sortedByDescending { it.fileSizeBytes }
            SortOption.STATUS -> result.sortedWith(compareByDescending<AddonItem> { it.isEnabled }.thenByDescending { it.dateAdded })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val loadedFolders = repository.getManagedFolders()
            _folders.value = loadedFolders
            if (_activeFolder.value == null && loadedFolders.isNotEmpty()) {
                _activeFolder.value = loadedFolders.firstOrNull { it.isDefault } ?: loadedFolders.first()
            }
            val loadedAddons = repository.loadAddons()
            _addons.value = loadedAddons
        }
    }

    fun toggleAddonEnabled(id: String) {
        viewModelScope.launch {
            val current = _addons.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index != -1) {
                val item = current[index]
                val updated = item.copy(isEnabled = !item.isEnabled)
                current[index] = updated
                _addons.value = current
                repository.saveAddons(current)
                refreshFoldersStats()
                val statusText = if (updated.isEnabled) {
                    if (_isMyanmarLanguage.value) "${updated.name} ကို အသုံးပြုရန် ဖွင့်လိုက်ပါပြီ" else "Enabled ${updated.name}"
                } else {
                    if (_isMyanmarLanguage.value) "${updated.name} ကို ပိတ်ထားလိုက်ပါပြီ" else "Disabled ${updated.name}"
                }
                _snackbarMessage.value = statusText
            }
        }
    }

    fun deleteAddon(id: String) {
        viewModelScope.launch {
            val current = _addons.value.toMutableList()
            val item = current.find { it.id == id }
            current.removeAll { it.id == id }
            _addons.value = current
            repository.saveAddons(current)
            refreshFoldersStats()
            val msg = if (_isMyanmarLanguage.value) "${item?.name ?: "Addon"} ကို ဖျက်လိုက်ပါပြီ" else "Deleted ${item?.name ?: "Addon"}"
            _snackbarMessage.value = msg
        }
    }

    fun renameAddon(id: String, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            val current = _addons.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index != -1) {
                val updated = current[index].copy(name = newName.trim())
                current[index] = updated
                _addons.value = current
                repository.saveAddons(current)
                _snackbarMessage.value = if (_isMyanmarLanguage.value) "အမည်ပြောင်းလဲပြီးပါပြီ" else "Renamed successfully"
            }
        }
    }

    fun moveAddonToFolder(id: String, folderName: String) {
        viewModelScope.launch {
            val current = _addons.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index != -1) {
                val updated = current[index].copy(targetFolderName = folderName)
                current[index] = updated
                _addons.value = current
                repository.saveAddons(current)
                refreshFoldersStats()
                val msg = if (_isMyanmarLanguage.value) "$folderName သို့ ရွှေ့ပြောင်းပြီးပါပြီ" else "Moved to $folderName"
                _snackbarMessage.value = msg
            }
        }
    }

    fun selectActiveFolder(folder: ManagedFolder) {
        _activeFolder.value = folder
        _folderPickerSheetOpen.value = false
        val msg = if (_isMyanmarLanguage.value) {
            "အသုံးပြုမည့် ဖိုဒါ: ${folder.nameMy}"
        } else {
            "Active Folder: ${folder.nameEn}"
        }
        _snackbarMessage.value = msg
    }

    fun addCustomSafFolder(uri: Uri, displayName: String) {
        viewModelScope.launch {
            val newFolder = repository.addCustomSafFolder(uri, displayName)
            val updatedList = repository.getManagedFolders()
            _folders.value = updatedList
            _activeFolder.value = newFolder
            _folderPickerSheetOpen.value = false
            val msg = if (_isMyanmarLanguage.value) {
                "စက်တွင်း ဖိုဒါ (${newFolder.nameEn}) ကို အောင်မြင်စွာ ရွေးချယ်ပြီးပါပြီ"
            } else {
                "Custom folder selected: ${newFolder.nameEn}"
            }
            _snackbarMessage.value = msg
        }
    }

    fun importAddonFromUri(uri: Uri) {
        viewModelScope.launch {
            val target = _activeFolder.value ?: _folders.value.firstOrNull()
            if (target != null) {
                val imported = repository.importAddonFromUri(uri, target)
                if (imported != null) {
                    val updated = repository.loadAddons()
                    _addons.value = updated
                    refreshFoldersStats()
                    val msg = if (_isMyanmarLanguage.value) {
                        "${imported.name} (${imported.fileFormat.extension}) ကို အောင်မြင်စွာ ထည့်သွင်းပြီးပါပြီ"
                    } else {
                        "Imported ${imported.name} (${imported.fileFormat.extension})"
                    }
                    _snackbarMessage.value = msg
                } else {
                    _snackbarMessage.value = if (_isMyanmarLanguage.value) "ဖိုင်ထည့်သွင်းခြင်း မအောင်မြင်ပါ" else "Failed to import addon file"
                }
            }
        }
    }

    fun openInspection(item: AddonItem) {
        _selectedAddonForInspection.value = item
        _inspectingFileContent.value = null
        _isInspectingLoading.value = true
        viewModelScope.launch {
            val entries = repository.inspectZipEntries(item)
            val icons = repository.getAddonPackIcons(item)
            _inspectingEntries.value = entries
            _inspectingPackIcons.value = icons
            _isInspectingLoading.value = false
        }
    }

    fun closeInspection() {
        _selectedAddonForInspection.value = null
        _inspectingEntries.value = emptyList()
        _inspectingPackIcons.value = emptyList()
        _inspectingFileContent.value = null
    }

    fun viewEntryContent(item: AddonItem, entryPath: String) {
        viewModelScope.launch {
            val content = repository.readZipEntryContent(item, entryPath)
            _inspectingFileContent.value = Pair(entryPath, content)
        }
    }

    fun closeEntryContent() {
        _inspectingFileContent.value = null
    }

    fun setLayoutMode(mode: ViewLayoutMode) {
        _layoutMode.value = mode
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: PackCategory) {
        _selectedCategory.value = category
    }

    fun setSort(sort: SortOption) {
        _selectedSort.value = sort
    }

    fun toggleLanguage() {
        _isMyanmarLanguage.value = !_isMyanmarLanguage.value
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun openFolderPicker() {
        _folderPickerSheetOpen.value = true
    }

    fun closeFolderPicker() {
        _folderPickerSheetOpen.value = false
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Batch Selection Operations
    fun toggleBatchSelection(id: String) {
        val current = _selectedBatchIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _selectedBatchIds.value = current
    }

    fun clearBatchSelection() {
        _selectedBatchIds.value = emptySet()
    }

    fun selectAllFiltered() {
        _selectedBatchIds.value = filteredAddons.value.map { it.id }.toSet()
    }

    fun enableAllSelected() {
        val selected = _selectedBatchIds.value
        if (selected.isEmpty()) return
        viewModelScope.launch {
            val current = _addons.value.map {
                if (selected.contains(it.id)) it.copy(isEnabled = true) else it
            }
            _addons.value = current
            repository.saveAddons(current)
            clearBatchSelection()
            refreshFoldersStats()
            _snackbarMessage.value = if (_isMyanmarLanguage.value) "ရွေးချယ်ထားသော အဒ်ဒွန်းအားလုံးကို ဖွင့်လိုက်ပါပြီ" else "Enabled all selected addons"
        }
    }

    fun disableAllSelected() {
        val selected = _selectedBatchIds.value
        if (selected.isEmpty()) return
        viewModelScope.launch {
            val current = _addons.value.map {
                if (selected.contains(it.id)) it.copy(isEnabled = false) else it
            }
            _addons.value = current
            repository.saveAddons(current)
            clearBatchSelection()
            refreshFoldersStats()
            _snackbarMessage.value = if (_isMyanmarLanguage.value) "ရွေးချယ်ထားသော အဒ်ဒွန်းအားလုံးကို ပိတ်လိုက်ပါပြီ" else "Disabled all selected addons"
        }
    }

    fun moveAllSelected(targetFolderName: String) {
        val selected = _selectedBatchIds.value
        if (selected.isEmpty()) return
        viewModelScope.launch {
            val current = _addons.value.map {
                if (selected.contains(it.id)) it.copy(targetFolderName = targetFolderName) else it
            }
            _addons.value = current
            repository.saveAddons(current)
            clearBatchSelection()
            refreshFoldersStats()
            _snackbarMessage.value = if (_isMyanmarLanguage.value) "$targetFolderName သို့ အားလုံးရွှေ့ပြောင်းပြီးပါပြီ" else "Moved selected to $targetFolderName"
        }
    }

    fun deleteAllSelected() {
        val selected = _selectedBatchIds.value
        if (selected.isEmpty()) return
        viewModelScope.launch {
            val current = _addons.value.filterNot { selected.contains(it.id) }
            _addons.value = current
            repository.saveAddons(current)
            clearBatchSelection()
            refreshFoldersStats()
            _snackbarMessage.value = if (_isMyanmarLanguage.value) "ရွေးချယ်ထားသည်များကို ဖျက်လိုက်ပါပြီ" else "Deleted selected addons"
        }
    }

    private suspend fun refreshFoldersStats() {
        _folders.value = repository.getManagedFolders()
    }
}
