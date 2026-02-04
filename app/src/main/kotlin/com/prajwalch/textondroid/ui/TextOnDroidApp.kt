package com.prajwalch.textondroid.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.prajwalch.textondroid.ui.editor.EditorScreen

import kotlinx.serialization.Serializable

@Serializable
private object Editor

@Composable
fun TextOnDroidApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Editor,
    ) {
        composable<Editor> {
            EditorScreen(onNavigateToSettings = {})
        }
    }
}