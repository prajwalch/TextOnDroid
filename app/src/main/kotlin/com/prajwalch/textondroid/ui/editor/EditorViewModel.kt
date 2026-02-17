package com.prajwalch.textondroid.ui.editor

import android.net.Uri

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.prajwalch.textondroid.data.DocumentRepository

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditorUiState(
    val title: String? = null,
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

    /** The editable text state of an editor text field. */
    val textFieldState = TextFieldState(initialText = "")

    /**
     * Whether it is possible to execute a meaningful undo action right now.
     * If this value is `false`, calling [undo] would be a no-op.
     */
    @OptIn(ExperimentalFoundationApi::class)
    val canUndo: Boolean get() = textFieldState.undoState.canUndo

    /**
     * Whether it is possible to execute a meaningful redo action right now.
     * If this value is `false`, calling [redo] would be a no-op.
     */
    @OptIn(ExperimentalFoundationApi::class)
    val canRedo: Boolean get() = textFieldState.undoState.canRedo

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

    private var documentJob: Job? = null

    init {
        openedDocumentUri.value?.let(::openDocument)
    }

    /** Opens the document pointed by the given URI. */
    @OptIn(ExperimentalFoundationApi::class)
    fun openDocument(documentUri: Uri) {
        documentJob?.cancel()
        documentJob = viewModelScope.launch {
            _uiState.update { it.copy(isDocumentLoading = true) }

            // Save Uri for later "save" operation.
            savedStateHandle[OPENED_DOCUMENT_URI_KEY] = documentUri.toString()

            documentRepository.openDocument(uri = documentUri)?.let { document ->
                _uiState.update { it.copy(title = document.title) }
                setContent(content = document.content)
            }
            _uiState.update { it.copy(isDocumentLoading = false) }

            observeContentChange {
                _uiState.update { it.copy(isDirty = true) }
            }
        }
    }

    /** Closes the currently opened document. */
    fun closeDocument() {
        savedStateHandle[OPENED_DOCUMENT_URI_KEY] = null

        documentJob?.cancel()
        clearContent()
        _uiState.update { it.copy(title = null, isDirty = false) }
    }

    /** Saves the currently opened document. */
    fun saveDocument() {
        viewModelScope.launch {
            val documentUri = openedDocumentUri.value ?: return@launch

            documentRepository.writeDocumentContent(
                uri = documentUri,
                content = getContent(),
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
                content = getContent(),
            )

            val title = documentRepository.readDocumentTitle(newDocumentUri)
            _uiState.update { it.copy(title = title, isDirty = false) }
        }
    }

    /**
     * Sets the content in [textFieldState], replacing any content that was
     * previously there, and places the cursor at the end.
     */
    @OptIn(ExperimentalFoundationApi::class)
    private fun setContent(content: CharSequence) {
        textFieldState.setTextAndPlaceCursorAtEnd(text = content.toString())
        textFieldState.undoState.clearHistory()
    }

    /** Returns the current content. */
    private fun getContent() = textFieldState.text.toString()

    /** Clears the current content. */
    private fun clearContent() {
        textFieldState.clearText()
    }

    /** Reverts the latest edit. */
    @OptIn(ExperimentalFoundationApi::class)
    fun undo() {
        textFieldState.undoState.undo()
    }

    /** Re-applies a change that was previously reverted via [undo]. */
    @OptIn(ExperimentalFoundationApi::class)
    fun redo() {
        textFieldState.undoState.redo()
    }

    private suspend fun observeContentChange(action: () -> Unit) {
        snapshotFlow { textFieldState.text }
            // Ignore initial text.
            .drop(1)
            .cancellable()
            .collect { action() }
    }

    private companion object {
        private const val OPENED_DOCUMENT_URI_KEY = "documentUri"
    }
}