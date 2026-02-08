package com.prajwalch.textondroid.ui.crash

import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import com.prajwalch.textondroid.R
import com.prajwalch.textondroid.constant.TextOnDroidConstants
import com.prajwalch.textondroid.ui.theme.spaces

@Composable
fun CrashScreen(
    stackTrace: String?,
    onExportCrashLogsToFile: (Uri) -> Unit,
    onRestartApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val exportLocationChooser = rememberLauncherForActivityResult(
        contract = CreateDocument(TextOnDroidConstants.LOGS_FILE_MIME_TYPE),
    ) { logFileUri ->
        logFileUri?.let(onExportCrashLogsToFile)
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(MaterialTheme.spaces.large),
            verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.spaces.large),
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(R.drawable.ic_bug_report),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
            CrashScreenTitle()
            CrashScreenSubtitle()
            stackTrace?.let {
                StackTraceCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    stackTrace = it,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spaces.small)) {
                ExportCrashLogsButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        exportLocationChooser.launch(TextOnDroidConstants.LOGS_FILE_NAME)
                    },
                )
                RestartApplicationButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRestartApp,
                )
            }
        }
    }
}

@Composable
private fun CrashScreenTitle(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.crash_screen_title),
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
private fun CrashScreenSubtitle(modifier: Modifier = Modifier) {
    val subtitle = stringResource(
        R.string.crash_screen_subtitle,
        stringResource(R.string.app_name),
    )
    Text(
        modifier = modifier,
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun StackTraceCard(
    stackTrace: String,
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    contentPadding: PaddingValues = PaddingValues(
        all = MaterialTheme.spaces.large,
    ),
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
    ) {
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(state = rememberScrollState()),
        ) {
            SelectionContainer { Text(text = stackTrace) }
        }
    }
}

@Composable
private fun ExportCrashLogsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(modifier = modifier, onClick = onClick) {
        Text(text = stringResource(R.string.crash_button_export_crash_logs))
    }
}

@Composable
private fun RestartApplicationButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(modifier = modifier, onClick = onClick) {
        Text(text = stringResource(R.string.crash_button_restart_application))
    }
}