package com.prajwalch.textondroid.ui.editor.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import com.prajwalch.textondroid.R
import com.prajwalch.textondroid.ui.theme.spaces

@Composable
fun Finder(
    onClose: () -> Unit,
    onFind: (String) -> Unit,
    onSelectNextOccurrence: () -> Unit,
    onSelectPreviousOccurrence: () -> Unit,
//    onReplace: (String) -> Unit,
//    onReplaceAll: (String) -> Unit,
    matchCase: Boolean,
    onToggleMatchCase: () -> Unit,
//    showReplaceField: Boolean,
//    onToggleReplaceField: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
//        val replaceFieldToggleIcon = if (showReplaceField) {
//            R.drawable.ic_keyboard_arrow_up
//        } else {
//            R.drawable.ic_keyboard_arrow_down
//        }
//
//        IconButton(onClick = onToggleReplaceField) {
//            Icon(
//                painter = painterResource(replaceFieldToggleIcon),
//                contentDescription = null,
//            )
//        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spaces.small),
        ) {
            FindField(
                onFind = onFind,
                onSelectNextOccurrence = onSelectNextOccurrence,
                onSelectPreviousOccurrence = onSelectPreviousOccurrence,
                onClose = onClose,
                matchCase = matchCase,
                onToggleMatchCase = onToggleMatchCase,
            )
//            AnimatedVisibility(visible = showReplaceField) {
//                ReplaceField(
//                    onReplace = onReplace,
//                    onReplaceAll = onReplaceAll,
//                )
//            }
        }
    }
}

@Composable
private fun FindField(
    onFind: (String) -> Unit,
    onSelectNextOccurrence: () -> Unit,
    onSelectPreviousOccurrence: () -> Unit,
    onClose: () -> Unit,
    matchCase: Boolean,
    onToggleMatchCase: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textFieldState = rememberTextFieldState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardActionHandler = KeyboardActionHandler {
        onFind(textFieldState.text.toString())
        keyboardController?.hide()
    }

    val defaultIconButtonColors = IconButtonDefaults.iconButtonColors()
    val activeIconButtonColors = defaultIconButtonColors.copy(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
    val iconButtonColors = { active: Boolean ->
        if (active) activeIconButtonColors else defaultIconButtonColors
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        FinderTextField(
            modifier = Modifier.weight(1f),
            state = textFieldState,
            placeholder = { Text(text = stringResource(R.string.editor_find_search_query_hint)) },
            trailingIcon = {
                IconButton(
                    onClick = onToggleMatchCase,
                    colors = iconButtonColors(matchCase),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_match_case),
                        contentDescription = null,
                    )
                }
            },
            onKeyboardAction = keyboardActionHandler,
        )
        IconButton(onClick = onSelectPreviousOccurrence) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_upward),
                contentDescription = null,
            )
        }
        IconButton(onClick = onSelectNextOccurrence) {
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

//@Composable
//private fun ReplaceField(
//    onReplace: (String) -> Unit,
//    onReplaceAll: (String) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    val textFieldState = rememberTextFieldState()
//
//    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
//        FinderTextField(
//            modifier = Modifier.weight(1f),
//            state = textFieldState,
//        )
//        IconButton(onClick = { onReplace(textFieldState.text.toString()) }) {
//            Icon(
//                painter = painterResource(R.drawable.ic_repeat_one),
//                contentDescription = null,
//            )
//        }
//        IconButton(onClick = { onReplaceAll(textFieldState.text.toString()) }) {
//            Icon(
//                painter = painterResource(R.drawable.ic_arrow_downward),
//                contentDescription = null,
//            )
//        }
//    }
//}

@Composable
private fun FinderTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState = rememberTextFieldState(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onKeyboardAction: KeyboardActionHandler? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        onKeyboardAction = onKeyboardAction,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
    )
}