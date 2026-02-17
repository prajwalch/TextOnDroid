package com.prajwalch.textondroid.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle

import com.prajwalch.textondroid.ui.theme.spaces

@Composable
fun SettingsGroup(
    name: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        SettingsGroupName(title = name)
        content()
    }
}


@Composable
fun SettingsGroupName(
    title: String,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(all = MaterialTheme.spaces.large),
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.titleSmall,
) {
    Text(
        modifier = modifier.padding(padding),
        text = title,
        color = color,
        style = style,
    )
}

@Composable
fun SettingsListItem(
    onClick: () -> Unit,
    @DrawableRes leadingIcon: Int,
    name: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier),
        leadingContent = {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = null,
            )
        },
        headlineContent = { Text(text = name) },
        supportingContent = summary?.let { { Text(text = it) } },
        trailingContent = trailingContent,
    )
}