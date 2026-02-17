package com.prajwalch.textondroid.di

import android.content.Context

import androidx.datastore.preferences.preferencesDataStore

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

val appModule = module {
    single { androidContext().contentResolver }
    single { androidContext().settingsDataStore }
}