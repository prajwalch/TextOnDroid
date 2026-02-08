package com.prajwalch.textondroid.ui.editor

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.charset.Charset

data class EditorUiState(
    val title: String? = null,
    val content: String = "",
    val isDirty: Boolean = false,
    val isDocumentLoading: Boolean = false,
    val isDocumentOpened: Boolean = false,
)

/** ViewModel which handles the business logic of core text editor. */
class EditorViewModel(
    private val contentResolver: ContentResolver,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    /** [Uri] of a currently opened document. */
    private val openedDocumentUri = savedStateHandle
        .getStateFlow<String?>(key = OPENED_DOCUMENT_URI_KEY, initialValue = null)
        .map { it?.toUri() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    /** Internal mutable UI state. */
    private val _uiState = MutableStateFlow(EditorUiState())

    /** Current public UI state. */
    val uiState = combine(
        _uiState,
        openedDocumentUri.map { it != null },
    ) { currentUiState, isDocumentOpened ->
        currentUiState.copy(isDocumentOpened = isDocumentOpened)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditorUiState(),
    )

    init {
        openedDocumentUri.value?.let(::openDocument)
    }

    /** Opens the document pointed by the given URI. */
    fun openDocument(documentUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDocumentLoading = true) }

            // Save Uri for later "save" operation.
            savedStateHandle[OPENED_DOCUMENT_URI_KEY] = documentUri.toString()

            val title = readDocumentTitle(documentUri)
            val content = readDocumentContent(documentUri)

            _uiState.update {
                it.copy(
                    title = title,
                    content = content ?: it.content,
                    isDocumentLoading = false,
                )
            }
        }
    }

    /** Closes the currently opened document. */
    fun closeDocument() {
        savedStateHandle[OPENED_DOCUMENT_URI_KEY] = null
        _uiState.update {
            it.copy(
                title = null,
                content = "",
                isDirty = false,
                isDocumentOpened = false,
            )
        }
    }

    /** Updates the current content with the given one. */
    fun updateDocumentContent(content: String) {
        _uiState.update { it.copy(content = content, isDirty = true) }
    }

    /** Saves the currently opened document. */
    fun saveDocument() {
        viewModelScope.launch(Dispatchers.IO) {
            val documentUri = openedDocumentUri.value ?: return@launch

            contentResolver.openBufferedWriter(documentUri)?.use {
                it.write(_uiState.value.content)
            }

            _uiState.update { it.copy(isDirty = false) }
        }
    }

    private suspend fun readDocumentTitle(documentUri: Uri) = withContext(Dispatchers.IO) {
        contentResolver.getDisplayName(uri = documentUri)
    }

    private suspend fun readDocumentContent(documentUri: Uri) = withContext(Dispatchers.IO) {
        contentResolver.openBufferedReader(uri = documentUri)?.use {
            buildString { it.forEachLine(::append) }
        }
    }

    private companion object {
        private const val OPENED_DOCUMENT_URI_KEY = "documentUri"
    }
}

private fun ContentResolver.openBufferedReader(
    uri: Uri,
    charset: Charset = Charsets.UTF_8,
): BufferedReader? {
    return this.openInputStream(uri)?.bufferedReader(charset = charset)
}

private fun ContentResolver.openBufferedWriter(
    uri: Uri,
    charset: Charset = Charsets.UTF_8,
): BufferedWriter? {
    return this.openOutputStream(uri)?.bufferedWriter(charset = charset)
}

private fun ContentResolver.getDisplayName(uri: Uri): String? {
    val cursor = this.query(
        /* uri = */
        uri,
        /* projection = */
        arrayOf(OpenableColumns.DISPLAY_NAME),
        /* selection = */
        null,
        /* selectionArgs = */
        null,
        /* sortOrder = */
        null,
        /* cancellationSignal = */
        null,
    ) ?: return null

    return cursor.use {
        if (!it.moveToFirst()) return@use null

        val displayNameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (displayNameColumnIndex != -1) it.getString(displayNameColumnIndex) else null
    }
}