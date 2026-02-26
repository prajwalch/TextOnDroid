package com.prajwalch.textondroid.ui.editor.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
    matchCase: Boolean,
    onToggleMatchCase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultIconButtonColors = IconButtonDefaults.iconButtonColors()
    val activeIconButtonColors = defaultIconButtonColors.copy(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
    val iconButtonColors = { active: Boolean ->
        if (active) activeIconButtonColors else defaultIconButtonColors
    }

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
            trailingIcon = {
                IconButton(
                    onClick = onToggleMatchCase,
                    colors = iconButtonColors(matchCase),
                ) {
                    Icon(
                        // TODO: Use correct icon.
                        painter = painterResource(R.drawable.ic_wrap_text),
                        contentDescription = null,
                    )
                }
            },
            lineLimits = TextFieldLineLimits.SingleLine,
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