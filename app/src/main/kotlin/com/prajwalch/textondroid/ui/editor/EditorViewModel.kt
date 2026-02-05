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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.charset.Charset

data class EditorUiState(
    val fileName: String? = null,
    val content: String = "",
)

/** ViewModel which handles the business logic of core text editor. */
class EditorViewModel(
    private val contentResolver: ContentResolver,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    /** Uri of a document. */
    private val documentUri get() = savedStateHandle.get<String>(DOCUMENT_URI_KEY)?.toUri()

    /** Current UI state. */
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val documentUri = documentUri ?: return@launch

            val fileName = contentResolver.getDisplayName(documentUri)
            _uiState.update { it.copy(fileName = fileName) }

            contentResolver.openBufferedReader(documentUri)?.use { contentReader ->
                updateContent(content = contentReader.readText())
            }
        }
    }

    /** Updates the URI of a document to operate on. */
    fun updateDocumentUri(uri: Uri) {
        viewModelScope.launch {
            savedStateHandle[DOCUMENT_URI_KEY] = uri.toString()

            val fileName = withContext(Dispatchers.IO) { contentResolver.getDisplayName(uri) }
            _uiState.update { it.copy(fileName = fileName) }
        }
    }

    /** Updates the current content with the given one. */
    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    /** Saves the current document. */
    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            val documentUri = documentUri ?: return@launch

            contentResolver.openBufferedWriter(documentUri)?.use {
                it.write(_uiState.value.content)
            }
        }
    }

    private companion object {
        private const val DOCUMENT_URI_KEY = "documentUri"
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