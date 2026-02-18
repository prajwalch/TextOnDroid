package com.prajwalch.textondroid.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.prajwalch.textondroid.data.SettingsRepository
import com.prajwalch.textondroid.domain.model.Theme

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val enableDynamicTheme: Boolean = true,
    val theme: Theme = Theme.Default,
    val enablePureBlack: Boolean = false,
    val wrapLines: Boolean = false,
)

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    val uiState = combine(
        settingsRepository.enableDynamicTheme,
        settingsRepository.theme,
        settingsRepository.enablePureBlack,
        settingsRepository.wrapLines,
        ::SettingsUiState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(),
    )

    fun enableDynamicTheme(enable: Boolean) {
        viewModelScope.launch { settingsRepository.enableDynamicTheme(enable) }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    fun enablePureBlack(enable: Boolean) {
        viewModelScope.launch { settingsRepository.enablePureBlack(enable) }
    }

    fun setWrapLines(wrap: Boolean) {
        viewModelScope.launch { settingsRepository.setWrapLines(wrap) }
    }
}