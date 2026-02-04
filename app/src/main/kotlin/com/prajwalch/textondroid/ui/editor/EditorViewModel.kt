package com.prajwalch.textondroid.ui.editor

import android.app.Application
import android.content.ContentResolver
import android.net.Uri

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.charset.Charset

data class EditorUiState(
    val fileName: String = "Untitled",
    val content: String = "",
)

/** ViewModel which handles the business logic of core text editor. */
class EditorViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    /**
     * Uri of a document to edit.
     *
     * If it's `null`, an empty buffered document is created.
     */
    private val documentUri = savedStateHandle.get<String>("documentUri")?.let(Uri::parse)

    /**
     * [android.content.ContentResolver] for underlying file handling.
     */
    private val contentResolver get() = application.contentResolver

    /** Current UI state. */
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (documentUri == null) return@launch

            contentResolver.openBufferedReader(documentUri)?.use { contentReader ->
                val content = contentReader.readText()
                _uiState.update { it.copy(content = content) }
            }
        }
    }

    /** Updates the current content with the given one. */
    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    /** Saves the current document. */
    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            if (documentUri == null) return@launch

            contentResolver.openBufferedWriter(documentUri)?.use {
                it.write(_uiState.value.content)
            }
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
}