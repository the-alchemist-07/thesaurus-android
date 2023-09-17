package com.mashood.thesaurus.app.common

/**
 * Converts the first letter of the given word to a capital letter
 */
fun String.capitalizeFirstLetter(): String = replaceFirstChar { firstChar ->
    firstChar.titlecase()
}
