package com.prajwalch.textondroid.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface

import com.prajwalch.textondroid.ui.theme.TextOnDroidTheme

import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")

        super.onCreate(savedInstanceState)

        val documentUri = getDocumentUri()
        Timber.d("Editing existing document?: ${documentUri != null}")

        enableEdgeToEdge()
        setContent {
            TextOnDroidTheme {
                Surface {
                    TextOnDroidApp(documentUri = documentUri.toString())
                }
            }
        }
    }

    /** Returns the URI of a user selected document if exists, otherwise `null`. */
    private fun getDocumentUri(): Uri? {
        Timber.d("getDocumentUri")

        if (intent == null) return null

        val action = intent.action
        val type = intent.type
        Timber.d("Action = $action, Type = $type")

        return when {
            action == Intent.ACTION_VIEW && type == "text/plain" -> intent.data
            else -> null
        }
    }
}