package com.prajwalch.textondroid

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface

import com.prajwalch.textondroid.ui.TextOnDroidApp
import com.prajwalch.textondroid.ui.theme.TextOnDroidTheme

import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TextOnDroidTheme {
                Surface {
                    TextOnDroidApp()
                }
            }
        }
    }
}