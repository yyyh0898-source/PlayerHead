package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Texture
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.MutedActiveGreen
import com.example.ui.theme.MutedAmber
import com.example.ui.theme.MutedIndigo
import com.example.ui.theme.NordicCyan

enum class PackType(
    val titleEn: String,
    val titleMy: String,
    val icon: ImageVector,
    val accentColor: Color
) {
    RESOURCE_PACK(
        titleEn = "Resource Pack",
        titleMy = "ရီဆို့စ် ပက်ခ် (RP)",
        icon = Icons.Default.Texture,
        accentColor = NordicCyan
    ),
    BEHAVIOR_PACK(
        titleEn = "Behavior Pack",
        titleMy = "ဘီဟေးဗီးယား ပက်ခ် (BP)",
        icon = Icons.Default.Extension,
        accentColor = MutedAmber
    ),
    PLAYER_HEADS(
        titleEn = "Player Heads / Skins",
        titleMy = "ကစားသမား ခေါင်း / စကင်း",
        icon = Icons.Default.Face,
        accentColor = MutedIndigo
    ),
    WORLD_TEMPLATE(
        titleEn = "World Template",
        titleMy = "ကမ္ဘာ့ တင်းပလိတ်",
        icon = Icons.Default.Public,
        accentColor = MutedActiveGreen
    ),
    SKIN_PACK(
        titleEn = "Skin Pack",
        titleMy = "စကင်း ပက်ခ်",
        icon = Icons.Default.Style,
        accentColor = MutedIndigo
    ),
    ARCHIVE(
        titleEn = "Archive Mod",
        titleMy = "ဇစ် မော်ဒ် ပက်ခ်",
        icon = Icons.Default.Archive,
        accentColor = Color(0xFF94A3B8)
    )
}

enum class FileFormat(val extension: String, val badge: String) {
    MCADDON(".mcaddon", "MCADDON"),
    MCPACK(".mcpack", "MCPACK"),
    ZIP(".zip", "ZIP"),
    MCWORLD(".mcworld", "MCWORLD")
}

data class AddonItem(
    val id: String,
    val name: String,
    val description: String,
    val author: String = "Unknown",
    val version: String = "1.0.0",
    val packType: PackType = PackType.RESOURCE_PACK,
    val fileFormat: FileFormat = FileFormat.ZIP,
    val fileSizeBytes: Long = 0L,
    val dateAdded: Long = System.currentTimeMillis(),
    val isEnabled: Boolean = true,
    val localFilePath: String? = null,
    val targetFolderName: String = "Default Storage",
    val tags: List<String> = emptyList(),
    val iconCachePath: String? = null,
    val internalAssetZip: String? = null,
    val fileCount: Int = 0,
    val uuid: String = ""
) {
    val formattedSize: String
        get() = when {
            fileSizeBytes >= 1024 * 1024 -> String.format("%.1f MB", fileSizeBytes / (1024.0 * 1024.0))
            fileSizeBytes >= 1024 -> String.format("%.0f KB", fileSizeBytes / 1024.0)
            else -> "$fileSizeBytes B"
        }
}

data class ManagedFolder(
    val id: String,
    val nameEn: String,
    val nameMy: String,
    val pathDisplay: String,
    val uriString: String? = null,
    val isCustomSaf: Boolean = false,
    val isDefault: Boolean = false,
    val addonCount: Int = 0,
    val totalSizeMb: Double = 0.0
)

enum class ViewLayoutMode(val titleEn: String, val titleMy: String) {
    DETAILED_CARDS(titleEn = "Detailed", titleMy = "ကတ်အပြည့်"),
    COMPACT_LIST(titleEn = "Compact", titleMy = "ကျစ်လျစ်"),
    GRID_MATRIX(titleEn = "Grid", titleMy = "ဇယား")
}

data class ZipEntryItem(
    val path: String,
    val name: String,
    val size: Long,
    val isDirectory: Boolean,
    val isJson: Boolean = false,
    val isImage: Boolean = false
) {
    val formattedSize: String
        get() = when {
            size >= 1024 * 1024 -> String.format("%.1f MB", size / (1024.0 * 1024.0))
            size >= 1024 -> String.format("%.1f KB", size / 1024.0)
            else -> "$size B"
        }
}

enum class SortOption(val titleEn: String, val titleMy: String) {
    RECENT("Newest Added", "အသစ်ဆုံး"),
    NAME("Name (A-Z)", "အမည်စဉ်"),
    SIZE("File Size", "ဖိုင်အရွယ်အစား"),
    STATUS("Active First", "အသုံးပြုနေဆဲ")
}

enum class PackCategory(val titleEn: String, val titleMy: String) {
    ALL("All Addons", "အားလုံး"),
    ACTIVE("Active Only", "ဖွင့်ထားသည်များ"),
    BEHAVIOR("Behavior (BP)", "ဘီဟေးဗီးယား"),
    RESOURCE("Resource (RP)", "ရီဆို့စ်"),
    HEADS("Player Heads", "ခေါင်း / စကင်း"),
    ARCHIVES("Zip Archives", "ဇစ် ဖိုင်များ")
}
