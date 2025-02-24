package com.mashood.thesaurus.shortcuts.data.repository

import com.google.firebase.database.DatabaseReference
import com.mashood.thesaurus.app.common.constants.Constants.PATH_UNINSTALL_FEEDBACK
import com.mashood.thesaurus.shortcuts.domain.model.UninstallFeedbackModel
import com.mashood.thesaurus.shortcuts.domain.repository.UninstallRepository
import javax.inject.Inject

class UninstallRepositoryImpl @Inject constructor(
    private val feedbackReference: DatabaseReference
) : UninstallRepository {

    override suspend fun saveFeedback(data: UninstallFeedbackModel) {
        feedbackReference.child(PATH_UNINSTALL_FEEDBACK).push().setValue(data)
    }

}