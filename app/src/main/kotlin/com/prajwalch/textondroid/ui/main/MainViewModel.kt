package com.prajwalch.textondroid.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.prajwalch.textondroid.data.SettingsRepository
import com.prajwalch.textondroid.domain.model.Theme

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MainUiState(
    val enableDynamicTheme: Boolean = true,
    val theme: Theme = Theme.Default,
    val enablePureBlack: Boolean = false,
)

class MainViewModel(settingsRepository: SettingsRepository) : ViewModel() {
    val uiState = combine(
        settingsRepository.enableDynamicTheme,
        settingsRepository.theme,
        settingsRepository.enablePureBlack,
        ::MainUiState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState(),
    )
}