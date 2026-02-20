package com.prajwalch.textondroid.ui.editor.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import com.prajwalch.textondroid.R

@Composable
fun CreateNewFileDialog(
    onCreate: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var fileName by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_note_add),
                contentDescription = null,
            )
        },
        title = { Text(text = stringResource(R.string.editor_dialog_create_new_file_title)) },
        text = {
            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                label = {
                    Text(
                        text = stringResource(
                            R.string.editor_dialog_create_new_file_text_field_label
                        )
                    )
                },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(fileName) },
                enabled = fileName.isNotBlank(),
            ) {
                Text(text = stringResource(R.string.editor_dialog_create_new_file_button_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(R.string.editor_dialog_create_new_file_button_cancel))
            }
        },
    )
}