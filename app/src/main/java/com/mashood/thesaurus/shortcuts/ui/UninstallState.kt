package com.mashood.thesaurus.shortcuts.ui

sealed class UninstallState {
    data class ShowMessage(val message: String): UninstallState()
    object ShowSubmitUi: UninstallState()
    object NavigateToSettings: UninstallState()
    object CloseActivity: UninstallState()
}