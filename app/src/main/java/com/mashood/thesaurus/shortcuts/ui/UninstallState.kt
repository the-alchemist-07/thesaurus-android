package com.mashood.thesaurus.shortcuts.ui

sealed class UninstallState {
    data class ShowMessage(val message: String): UninstallState()
    data object ShowSubmitUi: UninstallState()
    data object NavigateToSettings: UninstallState()
    data object CloseActivity: UninstallState()
}