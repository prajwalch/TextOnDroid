package com.prajwalch.textondroid.ui.settings

import android.os.Build

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.prajwalch.textondroid.R
import com.prajwalch.textondroid.domain.model.Theme
import com.prajwalch.textondroid.ui.component.SettingsGroup
import com.prajwalch.textondroid.ui.component.SettingsListItem
import com.prajwalch.textondroid.ui.theme.spaces

import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SettingsScreenTopBar(onNavigateBack = onNavigateBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(state = rememberScrollState()),
        ) {
            AppearanceSettings(
                enableDynamicTheme = uiState.enableDynamicTheme,
                onEnableDynamicTheme = viewModel::enableDynamicTheme,
                theme = uiState.theme,
                onSetTheme = viewModel::setTheme,
                enablePureBlack = uiState.enablePureBlack,
                onEnablePureBlack = viewModel::enablePureBlack,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                )
            }
        },
        title = { Text(text = stringResource(R.string.settings_screen_title)) },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun AppearanceSettings(
    enableDynamicTheme: Boolean,
    onEnableDynamicTheme: (Boolean) -> Unit,
    theme: Theme,
    onSetTheme: (Theme) -> Unit,
    enablePureBlack: Boolean,
    onEnablePureBlack: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsGroup(
        modifier = modifier,
        name = stringResource(R.string.settings_group_appearance),
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SettingsListItem(
                onClick = { onEnableDynamicTheme(!enableDynamicTheme) },
                leadingIcon = R.drawable.ic_palette,
                name = stringResource(R.string.settings_enable_dynamic_theme),
                summary = stringResource(R.string.settings_enable_dynamic_theme_summary),
                trailingContent = {
                    Switch(
                        checked = enableDynamicTheme,
                        onCheckedChange = onEnableDynamicTheme,
                    )
                },
            )
        }

        Box {
            var showThemeOptions by rememberSaveable(theme) { mutableStateOf(false) }

            SettingsListItem(
                onClick = { showThemeOptions = true },
                leadingIcon = R.drawable.ic_dark_mode,
                name = stringResource(R.string.settings_theme),
                summary = theme.displayName(),
            )
            ThemeOptionsDropdownMenu(
                expanded = showThemeOptions,
                onDismiss = { showThemeOptions = false },
                currentTheme = theme,
                onSetTheme = onSetTheme,
            )
        }

        SettingsListItem(
            onClick = { onEnablePureBlack(!enablePureBlack) },
            leadingIcon = R.drawable.ic_contrast,
            name = stringResource(R.string.settings_pure_black),
            summary = stringResource(R.string.settings_pure_black_summary),
            trailingContent = {
                Switch(
                    checked = enablePureBlack,
                    onCheckedChange = onEnablePureBlack,
                )
            },
        )
    }
}

@Composable
private fun ThemeOptionsDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    currentTheme: Theme,
    onSetTheme: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset(x = MaterialTheme.spaces.large, y = 0.dp),
    ) {
        Theme.entries.forEach {
            DropdownMenuItem(
                text = { Text(text = it.displayName()) },
                onClick = { onSetTheme(it) },
                leadingIcon = {
                    if (it == currentTheme) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun Theme.displayName(): String {
    val id = when (this) {
        Theme.Light -> R.string.theme_light
        Theme.Dark -> R.string.theme_dark
        Theme.FollowSystem -> R.string.theme_follow_system
    }

    return stringResource(id)
}