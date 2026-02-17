package com.prajwalch.textondroid.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

import com.prajwalch.textondroid.extension.childComposable
import com.prajwalch.textondroid.extension.parentComposable
import com.prajwalch.textondroid.ui.editor.EditorScreen
import com.prajwalch.textondroid.ui.settings.SettingsScreen

import kotlinx.serialization.Serializable

@Serializable
private data class Editor(val documentUri: String? = null)

@Serializable
private object Settings

@Composable
fun TextOnDroidApp(documentUri: String?, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Editor(documentUri = documentUri),
    ) {
        parentComposable<Editor> {
            EditorScreen(onNavigateToSettings = { navController.navigate(Settings) })
        }

        childComposable<Settings> {
            SettingsScreen(onNavigateBack = navController::navigateUp)
        }
    }
}