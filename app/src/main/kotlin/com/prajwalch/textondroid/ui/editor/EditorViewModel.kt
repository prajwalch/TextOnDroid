package com.prajwalch.textondroid.ui.editor

import android.net.Uri

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.prajwalch.textondroid.data.DocumentRepository
import com.prajwalch.textondroid.data.SettingsRepository

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents editor UI specific state.
 */
data class EditorUiState(
    /**
     * Document title.
     *
     * `null` indicates either document is not opened yet or failure to get
     * document name.
     *
     * **NOTE:** It shouldn't be strictly used to check whether any document is
     * opened or not. Consider using [isDocumentOpened] for that.
     */
    val title: String? = null,
    /**
     * Indicates whether there are unsaved changes or not.
     */
    val isDirty: Boolean = false,
    /**
     * Indicates whether the document open process is running or not.
     */
    val isDocumentLoading: Boolean = false,
    /**
     * Indicates any document opened or not.
     */
    val isDocumentOpened: Boolean = false,
    /**
     * `true` indicates that the underlying document file is either deleted
     * or moved to different location after it has been opened.
     */
    val isDocumentFileGone: Boolean = false,
    /**
     * Finder options.
     */
    val finderOptions: FinderOptions = FinderOptions(),
    /**
     * Current editor specific settings.
     */
    val settings: EditorSettings = EditorSettings(),
)

/**
 * Finder specific options.
 */
data class FinderOptions(
    val matchCase: Boolean = false,
)

/**
 * Editor specific settings.
 */
data class EditorSettings(
    val wrapLines: Boolean = false,
)

/** ViewModel which handles the business logic of editor screen. */
class EditorViewModel(
    private val documentRepository: DocumentRepository,
    private val settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    /** [Uri] of a currently opened document. */
    private var openedDocumentUri: Uri?
        get() = savedStateHandle.get<String?>(key = DOCUMENT_URI_KEY)?.toUri()
        set(uri) = savedStateHandle.set(key = DOCUMENT_URI_KEY, value = uri?.toString())

    /** Internal mutable UI state. */
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState = combine(
        _uiState,
        getEditorSettings(),
    ) { currentUiState, editorSettings ->
        currentUiState.copy(settings = editorSettings)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditorUiState(),
    )

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

    private val textFinder = TextFinder(textFieldState)

    /**
     * A background job running content changes observer.
     *
     * Job starts after a document is opened and canceled when document is closed.
     */
    private var contentObserverJob: Job? = null

    init {
        // Open initial document if given.
        openedDocumentUri?.let(::openDocument)
    }

    /** Returns a [Flow] of editor specific settings. */
    private fun getEditorSettings(): Flow<EditorSettings> =
        settingsRepository.wrapLines.map(::EditorSettings)

    /** Opens the document pointed by the given URI. */
    fun openDocument(documentUri: Uri) {
        contentObserverJob?.cancel()

        contentObserverJob = viewModelScope.launch {
            _uiState.value = EditorUiState(isDocumentLoading = true)

            // Save Uri for later "save" operation.
            openedDocumentUri = documentUri

            val document = documentRepository.openDocument(documentUri) ?: return@launch
            setContent(content = document.content)

            _uiState.update { currentUiState ->
                EditorUiState(
                    title = document.title,
                    isDocumentOpened = true,
                    settings = currentUiState.settings,
                )
            }

            observeContentChange { _uiState.update { it.copy(isDirty = true) } }
        }
    }

    /** Closes the currently opened document. */
    fun closeDocument() {
        _uiState.value = EditorUiState()

        openedDocumentUri = null
        contentObserverJob?.cancel()
        clearContent()
    }

    /** Saves the currently opened document. */
    fun saveDocument() {
        viewModelScope.launch {
            val documentUri = openedDocumentUri ?: return@launch

            val writeSucceed = documentRepository.writeDocumentContent(
                uri = documentUri,
                content = getContent(),
            )

            if (writeSucceed) {
                _uiState.update { it.copy(isDirty = false) }
            } else {
                _uiState.update { it.copy(isDocumentFileGone = true) }
                openedDocumentUri = null
            }
        }
    }

    /** Saves current document as a new one with the current content. */
    fun saveDocumentAs(newDocumentUri: Uri) {
        viewModelScope.launch {
            openedDocumentUri = newDocumentUri

            // Copy current content to new document.
            documentRepository.writeDocumentContent(
                uri = newDocumentUri,
                content = getContent(),
            )

            val title = documentRepository.readDocumentTitle(newDocumentUri)
            _uiState.update {
                it.copy(
                    title = title,
                    isDirty = false,
                    isDocumentFileGone = false,
                )
            }
        }
    }

    fun toggleMatchCase() {
        val toggledMatchCase = !_uiState.value.finderOptions.matchCase

        textFinder.matchCase = toggledMatchCase
        _uiState.update {
            val finderOptions = it.finderOptions.copy(matchCase = toggledMatchCase)
            it.copy(finderOptions = finderOptions)
        }
    }

    fun findNext(term: String) {
        val occurrence = textFinder.findNext(term) ?: return
        val selectionRange = TextRange(
            start = occurrence.startIndex,
            end = occurrence.endIndex,
        )

        textFieldState.edit { selection = selectionRange }
    }

    fun findPrevious(term: String) {
        val occurrence = textFinder.findPrevious(term) ?: return
        val selectionRange = TextRange(
            start = occurrence.startIndex,
            end = occurrence.endIndex,
        )

        textFieldState.edit { selection = selectionRange }
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
        private const val DOCUMENT_URI_KEY = "documentUri"
    }
}

private class TextFinder(private val textFieldState: TextFieldState) {
    data class Occurrence(val startIndex: Int, val endIndex: Int)

    var matchCase = false

    fun findNext(term: String): Occurrence? {
        val text = getText()
        if (text.isBlank()) return null

        val currentSelectionRange = getCurrentSelectionRange()
        val startIndex = currentSelectionRange?.second ?: 0

        val termStartIndex = text
            .indexOf(term, startIndex = startIndex, ignoreCase = !matchCase)
            .takeIf { it != -1 }
            ?: return null
        val termEndIndex = termStartIndex + term.length

        return Occurrence(termStartIndex, termEndIndex)
    }

    fun findPrevious(term: String): Occurrence? {
        val text = getText()
        if (text.isBlank()) return null

        val currentSelectionRange = getCurrentSelectionRange()
        val startIndex = currentSelectionRange?.first?.minus(1) ?: text.lastIndex

        val termStartIndex = text
            .lastIndexOf(term, startIndex = startIndex, ignoreCase = !matchCase)
            .takeIf { it != -1 }
            ?: return null
        val termEndIndex = termStartIndex + term.length

        return Occurrence(termStartIndex, termEndIndex)
    }

    private fun getText(): CharSequence = textFieldState.text

    private fun getCurrentSelectionRange(): Pair<Int, Int>? {
        if (textFieldState.selection.collapsed) return null

        val currentSelectionRange = textFieldState.selection
        return Pair(currentSelectionRange.start, currentSelectionRange.end)
    }
}