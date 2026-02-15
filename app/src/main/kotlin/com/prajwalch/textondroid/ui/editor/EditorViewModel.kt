package com.prajwalch.textondroid.ui.editor

import android.net.Uri

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.prajwalch.textondroid.data.DocumentRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditorUiState(
    val title: String? = null,
    val content: String = "",
    val isDirty: Boolean = false,
    val isDocumentLoading: Boolean = false,
    val isDocumentOpened: Boolean = false,
)

/** ViewModel which handles the business logic of core text editor. */
class EditorViewModel(
    private val documentRepository: DocumentRepository,
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

            val document = documentRepository.openDocument(uri = documentUri)
            _uiState.update {
                it.copy(
                    title = document?.title,
                    content = document?.content ?: it.content,
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
        viewModelScope.launch {
            val documentUri = openedDocumentUri.value ?: return@launch

            documentRepository.writeDocumentContent(
                uri = documentUri,
                content = _uiState.value.content,
            )
            _uiState.update { it.copy(isDirty = false) }
        }
    }

    /** Saves current document as a new one with the current content. */
    fun saveDocumentAs(newDocumentUri: Uri) {
        viewModelScope.launch {
            savedStateHandle[OPENED_DOCUMENT_URI_KEY] = newDocumentUri.toString()

            // Copy current content to new document.
            documentRepository.writeDocumentContent(
                uri = newDocumentUri,
                content = _uiState.value.content,
            )

            val title = documentRepository.readDocumentTitle(newDocumentUri)
            _uiState.update { it.copy(title = title, isDirty = false) }
        }
    }

    private companion object {
        private const val OPENED_DOCUMENT_URI_KEY = "documentUri"
    }
}