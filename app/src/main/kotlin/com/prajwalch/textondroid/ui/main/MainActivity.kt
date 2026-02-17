package com.prajwalch.textondroid.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.prajwalch.textondroid.domain.model.Theme
import com.prajwalch.textondroid.ui.TextOnDroidApp
import com.prajwalch.textondroid.ui.theme.TextOnDroidTheme

import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")

        super.onCreate(savedInstanceState)

        val documentUri = getDocumentUri()
        Timber.d("Editing existing document?: ${documentUri != null}")

        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = when (uiState.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                Theme.FollowSystem -> isSystemInDarkTheme()
            }

            TextOnDroidTheme(
                darkTheme = darkTheme,
                dynamicColor = uiState.enableDynamicTheme,
                pureBlack = uiState.enablePureBlack,
            ) {
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