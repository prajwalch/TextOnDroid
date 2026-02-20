package com.prajwalch.textondroid.ui.editor.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import com.prajwalch.textondroid.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreenTopBar(
    title: String?,
    onSave: () -> Unit,
    onSaveAs: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClose: () -> Unit,
    onFind: () -> Unit,
    onReplace: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    enableTextOperations: Boolean = true,
    enableUndo: Boolean = true,
    enableRedo: Boolean = true,
) {
    var showMoreOptions by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title ?: stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        actions = {
            SaveIconButton(onClick = onSave, enabled = enableTextOperations)
            UndoIconButton(onClick = onUndo, enabled = enableTextOperations && enableUndo)
            RedoIconButton(onClick = onRedo, enabled = enableTextOperations && enableRedo)

            Box {
                MoreVertIconButton(onClick = { showMoreOptions = true })
                TopBarMoreOptionsMenu(
                    expanded = showMoreOptions,
                    onDismiss = { showMoreOptions = false },
                    onClose = onClose,
                    onSaveAs = onSaveAs,
                    onFind = onFind,
                    onReplace = onReplace,
                    onNavigateToSettings = onNavigateToSettings,
                    enableTextOperations = enableTextOperations,
                )
            }
        }
    )
}

@Composable
private fun SaveIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_save),
            contentDescription = stringResource(R.string.editor_action_save),
        )
    }
}

@Composable
private fun UndoIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_undo),
            contentDescription = stringResource(R.string.editor_action_undo),
        )
    }
}

@Composable
private fun RedoIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_redo),
            contentDescription = stringResource(R.string.editor_action_redo),
        )
    }
}

@Composable
private fun MoreVertIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
    ) {
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
    onClose: () -> Unit,
    onSaveAs: () -> Unit,
    onFind: () -> Unit,
    onReplace: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    enableTextOperations: Boolean = true,
) {
    fun actionWithDismiss(action: () -> Unit): () -> Unit = {
        action()
        onDismiss()
    }

    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.editor_action_close)) },
            onClick = actionWithDismiss(onClose),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                )
            },
            enabled = enableTextOperations,
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.editor_action_save_as)) },
            onClick = actionWithDismiss(onSaveAs),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_save_as),
                    contentDescription = null,
                )
            },
            enabled = enableTextOperations,
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.editor_action_find)) },
            onClick = actionWithDismiss(onFind),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                )
            },
            enabled = enableTextOperations,
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
            enabled = enableTextOperations,
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