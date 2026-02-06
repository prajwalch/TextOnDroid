package com.prajwalch.textondroid.ui.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.prajwalch.textondroid.R
import com.prajwalch.textondroid.ui.theme.spaces

import org.koin.androidx.compose.koinViewModel

private const val PLAIN_DOCUMENT_MIME_TYPE = "text/plain"

@Composable
fun EditorScreen(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { documentUri ->
        documentUri?.let(viewModel::openDocument)
    }
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(PLAIN_DOCUMENT_MIME_TYPE),
    ) { documentUri ->
        documentUri?.let(viewModel::openDocument)
    }

    var showSaveAsDialog by rememberSaveable { mutableStateOf(false) }
    if (showSaveAsDialog) {
        SaveAsDialog(
            onDismiss = { showSaveAsDialog = false },
            onConfirm = { fileName ->
                createDocumentLauncher.launch(fileName)
                showSaveAsDialog = false
            },
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        topBar = {
            EditorScreenTopBar(
                title = uiState.title?.let { if (uiState.isDirty) "$it*" else it },
                onSave = viewModel::saveDocument,
                onUndo = {},
                onRedo = {},
                onFind = {},
                onReplace = {},
                onNavigateToSettings = onNavigateToSettings,
                enableTextOperations = uiState.isDocumentOpened,
            )
        },
    ) { innerPadding ->
        when {
            uiState.isDocumentLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.isDocumentOpened -> {
                TextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    value = uiState.content,
                    onValueChange = viewModel::updateDocumentContent,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            }

            else -> {
                WelcomePage(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = MaterialTheme.spaces.large),
                    onOpenFile = { openDocumentLauncher.launch(PLAIN_DOCUMENT_MIME_TYPE) },
                    onNewFile = { showSaveAsDialog = true },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorScreenTopBar(
    title: String?,
    onSave: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onFind: () -> Unit,
    onReplace: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    enableTextOperations: Boolean = true,
) {
    var showMoreOptions by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = { Text(text = title ?: stringResource(R.string.app_name)) },
        actions = {
            SaveIconButton(onClick = onSave, enabled = enableTextOperations)
            UndoIconButton(onClick = onUndo, enabled = enableTextOperations)
            RedoIconButton(onClick = onRedo, enabled = enableTextOperations)

            Box {
                MoreVertIconButton(onClick = { showMoreOptions = true })
                TopBarMoreOptionsMenu(
                    expanded = showMoreOptions,
                    onDismiss = { showMoreOptions = false },
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
    onFind: () -> Unit,
    onReplace: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    enableTextOperations: Boolean = true,
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

@Composable
private fun SaveAsDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var fileName by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_save_as),
                contentDescription = null,
            )
        },
        title = { Text(text = stringResource(R.string.editor_dialog_save_as_title)) },
        text = {
            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                label = {
                    Text(text = stringResource(R.string.editor_dialog_save_as_text_field_label))
                },
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(fileName) },
                enabled = fileName.isNotBlank(),
            ) {
                Text(text = stringResource(R.string.editor_dialog_save_as_button_done))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.editor_dialog_save_as_button_cancel))
            }
        },
    )
}

@Composable
private fun WelcomePage(
    onOpenFile: () -> Unit,
    onNewFile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Open file button
        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = onOpenFile,
        ) {
            Icon(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                painter = painterResource(R.drawable.ic_folder),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.editor_welcome_page_button_open_file))
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spaces.extraSmall))
        // New file button
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = onNewFile,
        ) {
            Icon(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                painter = painterResource(R.drawable.ic_note_add),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.editor_welcome_page_button_new_file))
        }
    }
}