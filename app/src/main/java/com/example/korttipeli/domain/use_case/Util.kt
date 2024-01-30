package com.example.korttipeli.domain.use_case

import java.util.*

object Util {
    fun cleanName(dirtyText: String) = dirtyText.removeMultipleSpaces().trim()
        .replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

    private fun String.removeMultipleSpaces(): String {
        return this.filterIndexed { i, char ->
            char != ' ' || !(i > 0 && this[i - 1] == ' ')
        }
    }
}