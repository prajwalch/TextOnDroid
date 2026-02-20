package com.prajwalch.textondroid.ui.editor.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import com.prajwalch.textondroid.R

@Composable
fun UnsavedChangesDialog(
    onDiscard: () -> Unit,
    onKeepEditing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onKeepEditing,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_warning),
                contentDescription = null,
            )
        },
        title = { Text(text = stringResource(R.string.editor_dialog_unsaved_changes_title)) },
        text = { Text(text = stringResource(R.string.editor_dialog_unsaved_changes_text)) },
        confirmButton = {
            TextButton(onClick = onDiscard) {
                Text(text = stringResource(R.string.editor_dialog_unsaved_changes_button_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onKeepEditing) {
                Text(text = stringResource(R.string.editor_dialog_unsaved_changes_button_dismiss))
            }
        },
    )
}