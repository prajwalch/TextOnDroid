package com.prajwalch.textondroid.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

import com.prajwalch.textondroid.domain.model.Document

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DocumentRepository(private val contentResolver: ContentResolver) {
    private val ioDispatcher = Dispatchers.IO

    suspend fun openDocument(uri: Uri): Document? = withContext(ioDispatcher) {
        val title = readDocumentTitle(uri) ?: return@withContext null
        val content = readDocumentContent(uri) ?: return@withContext null

        Document(title = title, content = content)
    }

    suspend fun readDocumentTitle(uri: Uri): String? = withContext(ioDispatcher) {
        val cursor = contentResolver.query(
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
        ) ?: return@withContext null

        cursor.use {
            if (!it.moveToFirst()) return@use null

            val displayNameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameColumnIndex != -1) it.getString(displayNameColumnIndex) else null
        }
    }

    private suspend fun readDocumentContent(uri: Uri): String? = withContext(ioDispatcher) {
        val bufferedReader = contentResolver.openInputStream(uri)?.bufferedReader()

        bufferedReader?.use {
            buildString { it.forEachLine(::append) }
        }
    }

    suspend fun writeDocumentContent(uri: Uri, content: String) {
        withContext(ioDispatcher) {
            val bufferedWriter = contentResolver.openOutputStream(uri)?.bufferedWriter()

            bufferedWriter?.use {
                it.write(content)
            }
        }
    }
}