package com.prajwalch.textondroid.ui.editor.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties

import com.prajwalch.textondroid.R

@Composable
fun DocumentFileGoneDialog(
    onCreateFile: () -> Unit,
    onCloseFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onCloseFile,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_warning),
                contentDescription = null,
            )
        },
        title = { Text(text = stringResource(R.string.editor_dialog_document_file_gone_title)) },
        text = { Text(text = stringResource(R.string.editor_dialog_document_file_gone_text)) },
        confirmButton = {
            TextButton(onClick = onCreateFile) {
                Text(text = stringResource(R.string.editor_dialog_document_file_gone_button_create_file))
            }
        },
        dismissButton = {
            TextButton(onClick = onCloseFile) {
                Text(text = stringResource(R.string.editor_dialog_document_file_gone_button_close_file))
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    )
}