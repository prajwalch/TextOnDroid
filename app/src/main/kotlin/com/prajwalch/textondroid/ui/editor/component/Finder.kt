package com.prajwalch.textondroid.ui.editor.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import com.prajwalch.textondroid.R

@Composable
fun Finder(
    onFindNext: (String) -> Unit,
    onFindPrevious: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val textFieldState = rememberTextFieldState()
        val textFieldColors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )

        TextField(
            modifier = Modifier.weight(1f), state = textFieldState,
            placeholder = { Text(text = stringResource(R.string.editor_find_search_query_hint)) },
            colors = textFieldColors,
        )

        IconButton(onClick = { onFindPrevious(textFieldState.text.toString()) }) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_upward),
                contentDescription = null,
            )
        }
        IconButton(onClick = { onFindNext(textFieldState.text.toString()) }) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_downward),
                contentDescription = null,
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = null,
            )
        }
    }
}