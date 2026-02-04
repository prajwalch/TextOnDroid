package com.prajwalch.textondroid.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prajwalch.textondroid.ui.editor.EditorScreen

import kotlinx.serialization.Serializable

@Serializable
private data class Editor(val documentUri: String? = null)

@Composable
fun TextOnDroidApp(documentUri: String?, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Editor(documentUri = documentUri),
    ) {
        composable<Editor> {
            EditorScreen(onNavigateToSettings = {})
        }
    }
}