package com.prajwalch.textondroid.util

import android.content.Context
import android.content.Intent

import com.prajwalch.textondroid.BuildConfig

class GlobalExceptionHandler(
    private val context: Context,
    private val activityToLaunch: Class<*>,
) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        startGivenActivity(exception)
        defaultHandler?.uncaughtException(thread, exception)
    }

    private fun startGivenActivity(exception: Throwable) {
        val crashIntent = Intent(context, activityToLaunch).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(EXTRA_CRASH_STACKTRACE, exception.stackTraceToString())
        }
        context.startActivity(crashIntent)
    }

    companion object {
        private const val EXTRA_CRASH_STACKTRACE = "${BuildConfig.APPLICATION_ID}.CRASH_STACKTRACE"

        fun getCrashStackTraceFromIntent(intent: Intent) =
            intent.getStringExtra(EXTRA_CRASH_STACKTRACE)
    }
}