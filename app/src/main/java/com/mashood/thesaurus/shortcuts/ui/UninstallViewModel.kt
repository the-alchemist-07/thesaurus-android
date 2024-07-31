package com.mashood.thesaurus.shortcuts.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mashood.thesaurus.BuildConfig
import com.mashood.thesaurus.R
import com.mashood.thesaurus.app.common.currentDateTime
import com.mashood.thesaurus.shortcuts.domain.model.UninstallFeedbackModel
import com.mashood.thesaurus.shortcuts.domain.repository.UninstallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UninstallViewModel @Inject constructor(
    private val application: Application,
    private val uninstallRepository: UninstallRepository
) : ViewModel() {

    var selectedReason: String? = null

    private val _state = Channel<UninstallState>()
    val state = _state.receiveAsFlow()

    fun validateData(comment: String?, selection: String) = viewModelScope.launch {
        if (selectedReason.isNullOrBlank()) {
            if (selection == UninstallActivity.SELECTION_UNINSTALL)
                _state.send(UninstallState.ShowMessage("Please select an option"))
            else {
                _state.send(UninstallState.ShowMessage("We would like to know why you decided to uninstall..."))
                _state.send(UninstallState.ShowSubmitUi)
            }
        } else if (selectedReason == application.resources.getString(R.string.uninstall_option5)
            && comment.isNullOrBlank()
        ) {
            _state.send(UninstallState.ShowMessage("Please let us know your whispers of wisdom..."))
        } else UninstallFeedbackModel(
            reason = selectedReason!!,
            dateTime = currentDateTime,
            selection = selection,
            comment = comment,
            isProduction = !BuildConfig.DEBUG
        ).also { saveData(it) }
    }

    private fun saveData(data: UninstallFeedbackModel) = viewModelScope.launch(Dispatchers.IO) {
        uninstallRepository.saveFeedback(data)

        if (data.selection == UninstallActivity.SELECTION_UNINSTALL) {
            withContext(Dispatchers.Main) {
                _state.send(UninstallState.NavigateToSettings)
            }
        } else withContext(Dispatchers.Main) {
            _state.send(UninstallState.CloseActivity)
        }
    }
}
