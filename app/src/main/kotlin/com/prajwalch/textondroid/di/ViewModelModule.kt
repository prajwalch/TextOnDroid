package com.prajwalch.textondroid.di

import com.prajwalch.textondroid.ui.editor.EditorViewModel

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::EditorViewModel)
}