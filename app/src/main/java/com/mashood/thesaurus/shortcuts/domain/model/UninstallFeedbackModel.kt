package com.mashood.thesaurus.shortcuts.domain.model

import androidx.annotation.Keep

@Keep
data class UninstallFeedbackModel(
    val key: String? = null,
    val reason: String,
    val comment: String? = null,
    val dateTime: String,
    val selection: String,
    val isProduction: Boolean
)
