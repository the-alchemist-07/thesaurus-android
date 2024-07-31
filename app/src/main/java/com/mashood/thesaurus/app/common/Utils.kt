package com.mashood.thesaurus.app.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Converts the first letter of the given word to a capital letter
 */
fun String.capitalizeFirstLetter(): String = replaceFirstChar { firstChar ->
    firstChar.titlecase()
}

/**
 * Provides the current date and time in dd MMM yyyy - HH:mm:ss
 */
val currentDateTime: String
    get() {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd MMM yyyy - HH:mm:ss", Locale.US)
        return formatter.format(time)
    }