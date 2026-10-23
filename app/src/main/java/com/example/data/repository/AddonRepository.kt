package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.data.model.AddonItem
import com.example.data.model.FileFormat
import com.example.data.model.ManagedFolder
import com.example.data.model.PackType
import com.example.data.model.ZipEntryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class AddonRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("addon_vault_prefs", Context.MODE_PRIVATE)
    private val addonsDir = File(context.filesDir, "addons").apply { mkdirs() }
    private val cacheIconsDir = File(context.cacheDir, "pack_icons").apply { mkdirs() }

    private val defaultFolders = listOf(
        ManagedFolder(
            id = "folder_default",
            nameEn = "AddonVault Local Storage",
            nameMy = "AddonVault ပင်မ သိုလှောင်ခန်း",
            pathDisplay = "Internal/AddonVault/Storage",
            isDefault = true,
            isCustomSaf = false
        ),
        ManagedFolder(
            id = "folder_bp",
            nameEn = "Minecraft Behavior Packs",
            nameMy = "Minecraft ဘီဟေးဗီးယား ဖိုဒါ (BP)",
            pathDisplay = "games/com.mojang/development_behavior_packs",
            isDefault = false,
            isCustomSaf = false
        ),
        ManagedFolder(
            id = "folder_rp",
            nameEn = "Minecraft Resource Packs",
            nameMy = "Minecraft ရီဆို့စ် ဖိုဒါ (RP)",
            pathDisplay = "games/com.mojang/development_resource_packs",
            isDefault = false,
            isCustomSaf = false
        ),
        ManagedFolder(
            id = "folder_downloads",
            nameEn = "Downloads / Game Addons",
            nameMy = "ဒေါင်းလုဒ် / ဂိမ်းအဒ်ဒွန်းများ",
            pathDisplay = "Download/Addons/Packs",
            isDefault = false,
            isCustomSaf = false
        )
    )

    suspend fun getManagedFolders(): List<ManagedFolder> = withContext(Dispatchers.IO) {
        val savedCustom = loadCustomFolders()
        val allFolders = defaultFolders + savedCustom
        val currentAddons = loadAddons()

        allFolders.map { folder ->
            val count = currentAddons.count { it.targetFolderName == folder.nameEn }
            val totalBytes = currentAddons.filter { it.targetFolderName == folder.nameEn }.sumOf { it.fileSizeBytes }
            folder.copy(
                addonCount = count,
                totalSizeMb = totalBytes / (1024.0 * 1024.0)
            )
        }
    }

    suspend fun addCustomSafFolder(uri: Uri, displayName: String): ManagedFolder = withContext(Dispatchers.IO) {
        val id = "saf_" + UUID.randomUUID().toString().take(8)
        val cleanName = displayName.ifBlank { "Custom Selected Folder" }
        val newFolder = ManagedFolder(
            id = id,
            nameEn = cleanName,
            nameMy = "စက်တွင်း ဖိုဒါ: $cleanName",
            pathDisplay = "SAF://${uri.path?.substringAfterLast(':') ?: uri.lastPathSegment ?: cleanName}",
            uriString = uri.toString(),
            isCustomSaf = true,
            isDefault = false
        )
        val current = loadCustomFolders().toMutableList()
        current.removeAll { it.uriString == uri.toString() }
        current.add(0, newFolder)
        saveCustomFolders(current)
        newFolder
    }

    suspend fun loadAddons(): List<AddonItem> = withContext(Dispatchers.IO) {
        val jsonString = prefs.getString("saved_addons_v1", null)
        if (jsonString != null) {
            try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<AddonItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(addonFromJson(obj))
                }
                if (list.isNotEmpty()) {
                    return@withContext list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Initialize default sample addons including the user's uploaded zip!
        val initialList = initInitialAddons()
        saveAddons(initialList)
        initialList
    }

    suspend fun saveAddons(addons: List<AddonItem>) = withContext(Dispatchers.IO) {
        val array = JSONArray()
        addons.forEach { item ->
            array.put(addonToJson(item))
        }
        prefs.edit().putString("saved_addons_v1", array.toString()).apply()
    }

    private fun initInitialAddons(): List<AddonItem> {
        val list = mutableListOf<AddonItem>()

        // 1. User's uploaded player heads zip from assets!
        val playerHeadsZip = File(addonsDir, "player-heads_c8040217.zip")
        var iconPath: String? = null
        var fileSize = 185198L

        try {
            if (!playerHeadsZip.exists()) {
                context.assets.open("player_heads_pack.zip").use { input ->
                    FileOutputStream(playerHeadsZip).use { output ->
                        input.copyTo(output)
                    }
                }
            }
            if (playerHeadsZip.exists()) {
                fileSize = playerHeadsZip.length()
                // Extract pack_icon0.png to cache
                val extractedIcon = File(cacheIconsDir, "pack_icon0.png")
                if (!extractedIcon.exists()) {
                    ZipFile(playerHeadsZip).use { zip ->
                        val entry = zip.getEntry("assets/images/pack_icon0.png")
                        if (entry != null) {
                            zip.getInputStream(entry).use { input ->
                                FileOutputStream(extractedIcon).use { out ->
                                    input.copyTo(out)
                                }
                            }
                        }
                    }
                }
                if (extractedIcon.exists()) {
                    iconPath = extractedIcon.absolutePath
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        list.add(
            AddonItem(
                id = "player_heads_pack_id",
                name = "Player Heads Generator & Models",
                description = "Custom player head geometries, textures, and attachable models for Minecraft Bedrock with 9 pack icons and attachable schemas.",
                author = "Community Addon Tool",
                version = "1.2.0",
                packType = PackType.PLAYER_HEADS,
                fileFormat = FileFormat.ZIP,
                fileSizeBytes = fileSize,
                dateAdded = System.currentTimeMillis() - 1000 * 60 * 30,
                isEnabled = true,
                localFilePath = playerHeadsZip.absolutePath,
                targetFolderName = "Minecraft Behavior Packs",
                tags = listOf("Heads", "Attachables", "Bedrock", "Models"),
                iconCachePath = iconPath,
                internalAssetZip = "player_heads_pack.zip",
                fileCount = 21,
                uuid = "c8040217-9c8a-4d3e-b712-4f8101a91e3b"
            )
        )

        list.add(
            AddonItem(
                id = "bare_bones_ui_id",
                name = "Minimalist Modern HUD & Dark UI",
                description = "Ultra-clean borderless game UI, AMOLED dark inventory panels, and streamlined icons for maximum visibility.",
                author = "Aero Minimal",
                version = "2.4.1",
                packType = PackType.RESOURCE_PACK,
                fileFormat = FileFormat.MCPACK,
                fileSizeBytes = 1845200L,
                dateAdded = System.currentTimeMillis() - 1000 * 60 * 180,
                isEnabled = true,
                localFilePath = null,
                targetFolderName = "Minecraft Resource Packs",
                tags = listOf("Clean", "Minimal", "AMOLED", "HUD"),
                iconCachePath = null,
                fileCount = 48,
                uuid = "7a29e1f0-4b33-4f9e-a81d-842bb563e411"
            )
        )

        list.add(
            AddonItem(
                id = "waypoints_bp_id",
                name = "Waypoints & Fast Teleports BP",
                description = "Interactive survival waypoints with beacon light columns, compass tracking, and custom marker entities.",
                author = "CraftLogic Dev",
                version = "1.0.5",
                packType = PackType.BEHAVIOR_PACK,
                fileFormat = FileFormat.MCADDON,
                fileSizeBytes = 3350100L,
                dateAdded = System.currentTimeMillis() - 1000 * 60 * 720,
                isEnabled = true,
                localFilePath = null,
                targetFolderName = "Minecraft Behavior Packs",
                tags = listOf("Survival", "Teleport", "Waypoints", "Entities"),
                iconCachePath = null,
                fileCount = 64,
                uuid = "e4d291fa-2234-4b5c-8971-d0092bf24177"
            )
        )

        list.add(
            AddonItem(
                id = "ambient_light_rp_id",
                name = "Dynamic Handheld Illumination RP",
                description = "Held torches, lanterns, and sea pickles emit dynamic real-time illumination while walking through caves.",
                author = "LumenTech",
                version = "3.1.0",
                packType = PackType.RESOURCE_PACK,
                fileFormat = FileFormat.MCPACK,
                fileSizeBytes = 4780900L,
                dateAdded = System.currentTimeMillis() - 1000 * 60 * 1440,
                isEnabled = false,
                localFilePath = null,
                targetFolderName = "Minecraft Resource Packs",
                tags = listOf("Lighting", "Shaders", "Caves", "RP"),
                iconCachePath = null,
                fileCount = 32,
                uuid = "1b88e09f-6712-4211-b0cf-619f71b29a00"
            )
        )

        list.add(
            AddonItem(
                id = "skyblock_evo_world_id",
                name = "SkyBlock Evolution 2026",
                description = "Floating survival islands world template with balanced custom trade economy, ore generators, and nether rift.",
                author = "BlockBuilders Lab",
                version = "2.0.0",
                packType = PackType.WORLD_TEMPLATE,
                fileFormat = FileFormat.MCWORLD,
                fileSizeBytes = 12900000L,
                dateAdded = System.currentTimeMillis() - 1000 * 60 * 2880,
                isEnabled = false,
                localFilePath = null,
                targetFolderName = "AddonVault Local Storage",
                tags = listOf("SkyBlock", "Template", "Economy", "Map"),
                iconCachePath = null,
                fileCount = 112,
                uuid = "99f430ca-a612-46e5-85df-924a4f89d311"
            )
        )

        return list
    }

    suspend fun importAddonFromUri(uri: Uri, targetFolder: ManagedFolder): AddonItem? = withContext(Dispatchers.IO) {
        try {
            var fileName = "imported_addon_${System.currentTimeMillis()}"
            var fileSize = 0L

            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex)
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }

            val destFile = File(addonsDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            if (fileSize == 0L && destFile.exists()) {
                fileSize = destFile.length()
            }

            // Determine format
            val lower = fileName.lowercase()
            val format = when {
                lower.endsWith(".mcaddon") -> FileFormat.MCADDON
                lower.endsWith(".mcpack") -> FileFormat.MCPACK
                lower.endsWith(".mcworld") -> FileFormat.MCWORLD
                else -> FileFormat.ZIP
            }

            // Determine pack type from extension and contents
            var packType = when (format) {
                FileFormat.MCADDON -> PackType.BEHAVIOR_PACK
                FileFormat.MCPACK -> PackType.RESOURCE_PACK
                FileFormat.MCWORLD -> PackType.WORLD_TEMPLATE
                FileFormat.ZIP -> PackType.ARCHIVE
            }

            var packName = fileName.substringBeforeLast(".")
            var packDesc = "Imported game addon from device storage."
            var packVersion = "1.0.0"
            var fileCount = 0
            var extractedIconPath: String? = null
            var parsedUuid = UUID.randomUUID().toString()

            // Inspect zip structure if valid zip
            try {
                ZipFile(destFile).use { zip ->
                    fileCount = zip.size()
                    // Check manifest.json
                    val manifestEntry = zip.getEntry("manifest.json") ?: zip.entries().asSequence().find { it.name.endsWith("manifest.json") }
                    if (manifestEntry != null) {
                        val text = zip.getInputStream(manifestEntry).bufferedReader().readText()
                        val json = JSONObject(text)
                        val header = json.optJSONObject("header")
                        if (header != null) {
                            packName = header.optString("name", packName)
                            packDesc = header.optString("description", packDesc)
                            val verArr = header.optJSONArray("version")
                            if (verArr != null && verArr.length() >= 3) {
                                packVersion = "${verArr.getInt(0)}.${verArr.getInt(1)}.${verArr.getInt(2)}"
                            }
                            parsedUuid = header.optString("uuid", parsedUuid)
                        }
                        val modules = json.optJSONArray("modules")
                        if (modules != null && modules.length() > 0) {
                            val modType = modules.getJSONObject(0).optString("type")
                            if (modType.contains("data") || modType.contains("script")) {
                                packType = PackType.BEHAVIOR_PACK
                            } else if (modType.contains("resources")) {
                                packType = PackType.RESOURCE_PACK
                            } else if (modType.contains("skin")) {
                                packType = PackType.SKIN_PACK
                            }
                        }
                    }

                    // Extract pack_icon.png if exists
                    val iconEntry = zip.getEntry("pack_icon.png") ?: zip.entries().asSequence().find { it.name.contains("pack_icon") && it.name.endsWith(".png") }
                    if (iconEntry != null) {
                        val cachedIcon = File(cacheIconsDir, "imported_${destFile.nameWithoutExtension}_icon.png")
                        zip.getInputStream(iconEntry).use { input ->
                            FileOutputStream(cachedIcon).use { out -> input.copyTo(out) }
                        }
                        if (cachedIcon.exists()) {
                            extractedIconPath = cachedIcon.absolutePath
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val newItem = AddonItem(
                id = UUID.randomUUID().toString(),
                name = packName,
                description = packDesc,
                author = "Imported",
                version = packVersion,
                packType = packType,
                fileFormat = format,
                fileSizeBytes = fileSize,
                dateAdded = System.currentTimeMillis(),
                isEnabled = true,
                localFilePath = destFile.absolutePath,
                targetFolderName = targetFolder.nameEn,
                tags = listOf("Imported", format.badge),
                iconCachePath = extractedIconPath,
                fileCount = fileCount,
                uuid = parsedUuid
            )

            val current = loadAddons().toMutableList()
            current.add(0, newItem)
            saveAddons(current)
            newItem
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun inspectZipEntries(item: AddonItem): List<ZipEntryItem> = withContext(Dispatchers.IO) {
        val file = getAddonFile(item) ?: return@withContext emptyList()
        val list = mutableListOf<ZipEntryItem>()
        try {
            ZipFile(file).use { zip ->
                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val lower = entry.name.lowercase()
                    list.add(
                        ZipEntryItem(
                            path = entry.name,
                            name = entry.name.substringAfterLast('/'),
                            size = entry.size.coerceAtLeast(0L),
                            isDirectory = entry.isDirectory,
                            isJson = lower.endsWith(".json"),
                            isImage = lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".webp")
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        list.sortedWith(compareBy({ !it.isDirectory }, { it.path }))
    }

    suspend fun readZipEntryContent(item: AddonItem, entryPath: String): String = withContext(Dispatchers.IO) {
        val file = getAddonFile(item) ?: return@withContext "File not found"
        try {
            ZipFile(file).use { zip ->
                val entry = zip.getEntry(entryPath) ?: return@withContext "Entry $entryPath not found in archive"
                zip.getInputStream(entry).bufferedReader().use { it.readText() }
            }
        } catch (e: Exception) {
            "Error reading entry: ${e.localizedMessage}"
        }
    }

    suspend fun getAddonPackIcons(item: AddonItem): List<String> = withContext(Dispatchers.IO) {
        val file = getAddonFile(item) ?: return@withContext emptyList()
        val iconPaths = mutableListOf<String>()
        try {
            ZipFile(file).use { zip ->
                val entries = zip.entries()
                var index = 0
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    if (!entry.isDirectory && (entry.name.contains("pack_icon") || entry.name.endsWith(".png"))) {
                        val iconFile = File(cacheIconsDir, "${item.id}_icon_${index++}.png")
                        if (!iconFile.exists()) {
                            zip.getInputStream(entry).use { input ->
                                FileOutputStream(iconFile).use { out -> input.copyTo(out) }
                            }
                        }
                        if (iconFile.exists()) {
                            iconPaths.add(iconFile.absolutePath)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        iconPaths
    }

    private fun getAddonFile(item: AddonItem): File? {
        if (item.localFilePath != null) {
            val f = File(item.localFilePath)
            if (f.exists()) return f
        }
        val defaultZip = File(addonsDir, "player-heads_c8040217.zip")
        if (defaultZip.exists()) return defaultZip
        return null
    }

    private fun loadCustomFolders(): List<ManagedFolder> {
        val raw = prefs.getString("custom_folders_v1", null) ?: return emptyList()
        val list = mutableListOf<ManagedFolder>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ManagedFolder(
                        id = obj.getString("id"),
                        nameEn = obj.getString("nameEn"),
                        nameMy = obj.optString("nameMy", obj.getString("nameEn")),
                        pathDisplay = obj.getString("pathDisplay"),
                        uriString = obj.optString("uriString", null),
                        isCustomSaf = obj.optBoolean("isCustomSaf", true),
                        isDefault = false
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveCustomFolders(folders: List<ManagedFolder>) {
        val array = JSONArray()
        folders.forEach { f ->
            val obj = JSONObject()
            obj.put("id", f.id)
            obj.put("nameEn", f.nameEn)
            obj.put("nameMy", f.nameMy)
            obj.put("pathDisplay", f.pathDisplay)
            obj.put("uriString", f.uriString)
            obj.put("isCustomSaf", f.isCustomSaf)
            array.put(obj)
        }
        prefs.edit().putString("custom_folders_v1", array.toString()).apply()
    }

    private fun addonToJson(item: AddonItem): JSONObject {
        val obj = JSONObject()
        obj.put("id", item.id)
        obj.put("name", item.name)
        obj.put("description", item.description)
        obj.put("author", item.author)
        obj.put("version", item.version)
        obj.put("packType", item.packType.name)
        obj.put("fileFormat", item.fileFormat.name)
        obj.put("fileSizeBytes", item.fileSizeBytes)
        obj.put("dateAdded", item.dateAdded)
        obj.put("isEnabled", item.isEnabled)
        obj.put("localFilePath", item.localFilePath)
        obj.put("targetFolderName", item.targetFolderName)
        obj.put("iconCachePath", item.iconCachePath)
        obj.put("internalAssetZip", item.internalAssetZip)
        obj.put("fileCount", item.fileCount)
        obj.put("uuid", item.uuid)

        val tagsArray = JSONArray()
        item.tags.forEach { tagsArray.put(it) }
        obj.put("tags", tagsArray)
        return obj
    }

    private fun addonFromJson(obj: JSONObject): AddonItem {
        val tags = mutableListOf<String>()
        val tagsArr = obj.optJSONArray("tags")
        if (tagsArr != null) {
            for (i in 0 until tagsArr.length()) {
                tags.add(tagsArr.getString(i))
            }
        }

        return AddonItem(
            id = obj.getString("id"),
            name = obj.getString("name"),
            description = obj.optString("description", ""),
            author = obj.optString("author", "Unknown"),
            version = obj.optString("version", "1.0.0"),
            packType = try {
                PackType.valueOf(obj.getString("packType"))
            } catch (e: Exception) {
                PackType.RESOURCE_PACK
            },
            fileFormat = try {
                FileFormat.valueOf(obj.getString("fileFormat"))
            } catch (e: Exception) {
                FileFormat.ZIP
            },
            fileSizeBytes = obj.optLong("fileSizeBytes", 0L),
            dateAdded = obj.optLong("dateAdded", System.currentTimeMillis()),
            isEnabled = obj.optBoolean("isEnabled", true),
            localFilePath = obj.optString("localFilePath", null),
            targetFolderName = obj.optString("targetFolderName", "Default Storage"),
            tags = tags,
            iconCachePath = obj.optString("iconCachePath", null),
            internalAssetZip = obj.optString("internalAssetZip", null),
            fileCount = obj.optInt("fileCount", 0),
            uuid = obj.optString("uuid", "")
        )
    }
}
