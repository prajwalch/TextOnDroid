package com.prajwalch.textondroid.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import com.prajwalch.textondroid.R

@Composable
fun EditorScreen(onNavigateToSettings: () -> Unit, modifier: Modifier = Modifier) {
    val textFieldState = rememberTextFieldState("")

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        topBar = {
            EditorScreenTopBar(
                title = "Untitled document",
                onSave = {},
                onUndo = {},
                onRedo = {},
                onFind = {},
                onReplace = {},
                onNavigateToSettings = onNavigateToSettings,
            )
        },
    ) { innerPadding ->
        TextField(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            state = textFieldState,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorScreenTopBar(
    title: String,
    onSave: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onFind: () -> Unit,
    onReplace: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMoreOptions by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = { Text(text = title) },
        actions = {
            SaveIconButton(onClick = onSave)
            UndoIconButton(onClick = onUndo)
            RedoIconButton(onClick = onRedo)

            Box {
                MoreVertIconButton(onClick = { showMoreOptions = true })
                TopBarMoreOptionsMenu(
                    expanded = showMoreOptions,
                    onDismiss = { showMoreOptions = false },
                    onFind = onFind,
                    onReplace = onReplace,
                    onNavigateToSettings = onNavigateToSettings,
                )
            }
        }
    )
}

@Composable
private fun SaveIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_save),
            contentDescription = stringResource(R.string.editor_action_save),
        )
    }
}

@Composable
private fun UndoIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_undo),
            contentDescription = stringResource(R.string.editor_action_undo),
        )
    }
}

@Composable
private fun RedoIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_redo),
            contentDescription = stringResource(R.string.editor_action_redo),
        )
    }
}

@Composable
private fun MoreVertIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_more_vert),
            contentDescription = stringResource(R.string.editor_action_more_options),
        )
    }
}

@Composable
private fun TopBarMoreOptionsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onFind: () -> Unit,
    onReplace: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    fun actionWithDismiss(action: () -> Unit) = {
        action()
        onDismiss()
    }

    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.editor_action_find)) },
            onClick = actionWithDismiss(onFind),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                )
            },
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.editor_action_replace)) },
            onClick = actionWithDismiss(onReplace),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_find_replace),
                    contentDescription = null,
                )
            },
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.editor_action_settings)) },
            onClick = actionWithDismiss(onNavigateToSettings),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = null,
                )
            },
        )
    }
}