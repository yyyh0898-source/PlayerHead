package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AddonVaultScreen
import com.example.ui.AddonViewModel
import com.example.ui.theme.AddonVaultTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AddonViewModel by viewModels()

    // SAF Directory Picker for choosing any custom storage folder
    private val folderPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val displayName = uri.path?.substringAfterLast(':')
                ?: uri.lastPathSegment
                ?: "Selected Storage Folder"
            viewModel.addCustomSafFolder(uri, displayName)
        }
    }

    // SAF File Picker for importing addon files (.zip, .mcaddon, .mcpack, .mcworld)
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewModel.importAddonFromUri(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            AddonVaultTheme(darkTheme = isDarkTheme) {
                AddonVaultScreen(
                    viewModel = viewModel,
                    onLaunchFilePicker = {
                        filePickerLauncher.launch(
                            arrayOf(
                                "application/zip",
                                "application/octet-stream",
                                "application/x-zip-compressed",
                                "*/*"
                            )
                        )
                    },
                    onLaunchFolderPicker = {
                        folderPickerLauncher.launch(null)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
