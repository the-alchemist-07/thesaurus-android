package com.mashood.thesaurus.shortcuts.domain.repository

import com.mashood.thesaurus.shortcuts.domain.model.UninstallFeedbackModel

interface UninstallRepository {

    suspend fun saveFeedback(data: UninstallFeedbackModel)

}