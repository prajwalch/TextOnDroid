package com.prajwalch.textondroid.ui.editor.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape

import com.prajwalch.textondroid.ui.theme.spaces

@Composable
fun EditorTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState = rememberTextFieldState(),
    wrapLines: Boolean = false,
) {
    val wrapLinesModifier = if (!wrapLines) {
        Modifier.horizontalScroll(state = rememberScrollState())
    } else {
        Modifier
    }

    TextField(
        modifier = modifier.then(wrapLinesModifier),
        state = state,
        lineLimits = TextFieldLineLimits.MultiLine(),
        shape = RectangleShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        contentPadding = PaddingValues(horizontal = MaterialTheme.spaces.large),
    )
}