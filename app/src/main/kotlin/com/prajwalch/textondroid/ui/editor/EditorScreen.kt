package com.prajwalch.textondroid.ui.editor

import android.net.Uri

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.prajwalch.textondroid.R
import com.prajwalch.textondroid.ui.editor.component.CreateNewFileDialog
import com.prajwalch.textondroid.ui.editor.component.DocumentFileGoneDialog
import com.prajwalch.textondroid.ui.editor.component.EditorScreenTopBar
import com.prajwalch.textondroid.ui.editor.component.EditorTextField
import com.prajwalch.textondroid.ui.editor.component.Finder
import com.prajwalch.textondroid.ui.editor.component.SaveAsDialog
import com.prajwalch.textondroid.ui.editor.component.UnsavedChangesDialog
import com.prajwalch.textondroid.ui.theme.spaces

import kotlinx.coroutines.FlowPreview
import org.koin.androidx.compose.koinViewModel

private const val PLAIN_DOCUMENT_MIME_TYPE = "text/plain"

@OptIn(FlowPreview::class)
@Composable
fun EditorScreen(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val saveAsDocumentLauncher = rememberCreateDocumentLauncher(viewModel::saveDocumentAs)
    var showSaveAsDialog by rememberSaveable { mutableStateOf(false) }
    if (showSaveAsDialog) {
        SaveAsDialog(
            onSave = { fileName ->
                saveAsDocumentLauncher.launch(fileName)
                showSaveAsDialog = false
            },
            onCancel = { showSaveAsDialog = false },
        )
    }

    var showUnsavedChangesDialog by rememberSaveable(uiState.isDocumentOpened) {
        mutableStateOf(false)
    }
    if (showUnsavedChangesDialog) {
        UnsavedChangesDialog(
            onDiscard = viewModel::closeDocument,
            onKeepEditing = { showUnsavedChangesDialog = false },
        )
    }

    if (uiState.isDocumentFileGone) {
        DocumentFileGoneDialog(
            onCreateFile = { saveAsDocumentLauncher.launch(uiState.title!!) },
            onCloseFile = { viewModel.closeDocument() },
        )
    }

    var showFindField by rememberSaveable { mutableStateOf(false) }
    var showReplaceField by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        topBar = {
            val title = if (uiState.isDocumentOpened) {
                uiState.title?.let { if (uiState.isDirty) "$it*" else it }
            } else {
                null
            }
            EditorScreenTopBar(
                title = title,
                onSave = viewModel::saveDocument,
                onSaveAs = { showSaveAsDialog = true },
                onUndo = viewModel::undo,
                onRedo = viewModel::redo,
                onClose = {
                    if (uiState.isDirty) {
                        showUnsavedChangesDialog = true
                    } else {
                        viewModel.closeDocument()
                    }
                },
                onFind = { showFindField = true; showReplaceField = false },
                onReplace = { showFindField = true; showReplaceField = true },
                onNavigateToSettings = onNavigateToSettings,
                enableTextOperations = uiState.isDocumentOpened,
                enableUndo = viewModel.canUndo,
                enableRedo = viewModel.canRedo,
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
                Column(modifier = Modifier.padding(innerPadding)) {
                    AnimatedVisibility(visible = showFindField) {
                        Finder(
                            onClose = { showFindField = false; showReplaceField = false },
                            onFind = viewModel::findAllOccurrences,
                            onSelectNextOccurrence = viewModel::selectNextOccurrence,
                            onSelectPreviousOccurrence = viewModel::selectPreviousOccurrence,
                            onReplace = viewModel::replaceSelectedOccurrence,
                            onReplaceAll = viewModel::replaceAllOccurrences,
                            matchCase = uiState.finderOptions.matchCase,
                            onToggleMatchCase = viewModel::toggleMatchCase,
                            showReplaceField = showReplaceField,
                            onToggleReplaceField = { showReplaceField = !showReplaceField },
                        )
                    }
                    EditorTextField(
                        modifier = Modifier.weight(1f),
                        state = viewModel.textFieldState,
                        wrapLines = uiState.settings.wrapLines,
                    )
                }
            }

            else -> {
                WelcomePage(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = MaterialTheme.spaces.large),
                    onOpenDocument = viewModel::openDocument,
                )
            }
        }
    }
}

@Composable
private fun WelcomePage(
    onOpenDocument: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val openDocumentLauncher = rememberOpenDocumentLauncher(onResult = onOpenDocument)
    val createDocumentLauncher = rememberCreateDocumentLauncher(onResult = onOpenDocument)

    var showCreateNewFileDialog by rememberSaveable { mutableStateOf(false) }
    if (showCreateNewFileDialog) {
        CreateNewFileDialog(
            onCreate = { fileName ->
                createDocumentLauncher.launch(fileName)
                showCreateNewFileDialog = false
            },
            onCancel = { showCreateNewFileDialog = false },
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Open file button
        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = { openDocumentLauncher.launch(arrayOf(PLAIN_DOCUMENT_MIME_TYPE)) },
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
            onClick = { showCreateNewFileDialog = true },
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

@Composable
private fun rememberOpenDocumentLauncher(
    onResult: (Uri) -> Unit,
): ManagedActivityResultLauncher<Array<String>, Uri?> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let(onResult)
    }
}

@Composable
private fun rememberCreateDocumentLauncher(
    onResult: (Uri) -> Unit,
    mimeType: String = PLAIN_DOCUMENT_MIME_TYPE,
): ManagedActivityResultLauncher<String, Uri?> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(mimeType = mimeType),
    ) { uri ->
        uri?.let(onResult)
    }
}