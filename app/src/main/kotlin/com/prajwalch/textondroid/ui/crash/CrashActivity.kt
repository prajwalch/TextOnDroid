package com.prajwalch.textondroid.ui.crash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope

import com.prajwalch.textondroid.R
import com.prajwalch.textondroid.ui.main.MainActivity
import com.prajwalch.textondroid.ui.theme.TextOnDroidTheme
import com.prajwalch.textondroid.util.GlobalExceptionHandler
import com.prajwalch.textondroid.util.LogsExporter

import kotlinx.coroutines.launch

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashStackTrace = GlobalExceptionHandler.getCrashStackTraceFromIntent(intent)

        enableEdgeToEdge()
        setContent {
            TextOnDroidTheme {
                CrashScreen(
                    stackTrace = crashStackTrace,
                    onExportCrashLogsToFile = {
                        exportCrashLogsToFile(fileUri = it, stackTrace = crashStackTrace)
                    },
                    onRestartApp = ::restartApplication,
                )
            }
        }
    }

    private fun exportCrashLogsToFile(fileUri: Uri, stackTrace: String?) {
        lifecycleScope.launch {
            val outputStream = contentResolver.openOutputStream(fileUri) ?: return@launch

            LogsExporter.exportLogsToOutputStream(
                outputStream = outputStream,
                stackTrace = stackTrace,
            )
        }.invokeOnCompletion {
            val successMessage = getString(R.string.crash_message_logs_exported)
            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun restartApplication() {
        finishAffinity()

        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
    }
}