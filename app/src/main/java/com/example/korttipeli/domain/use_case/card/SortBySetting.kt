package com.example.korttipeli.domain.use_case.card

import com.example.korttipeli.domain.model.CardUi
import javax.inject.Inject

class SortBySetting @Inject constructor() {

    operator fun invoke(
        unsortedList: List<CardUi>,
        sortingSetting: SortingSetting,
        username: String
    ): List<CardUi> {
        /*
        Explanation of modifier weights (if said modifier is set to true), upper overrides lower:
        1. Author's own cards put on top of list
        2. Card authors separated
        3. Cards types separated
        4. Sorting alphabetically either in ascending or descending order
        */

        //4
        var listInProgress = unsortedList.sortedBy { it.title }
        if (!sortingSetting.ascending) listInProgress = listInProgress.reversed()

        //3
        if (sortingSetting.separateTypes) listInProgress = listInProgress.sortedBy { it.type }

        //2
        if (sortingSetting.separateAuthors) {
            listInProgress = if (sortingSetting.ascending) {
                listInProgress.sortedBy { it.author }
            } else listInProgress.sortedByDescending { it.author }
        }


        //1
        if (sortingSetting.showOwnOnTop) {
            val cardsOnTop = listInProgress.filter { it.author == username }
            val otherCards = listInProgress.filterNot { it.author == username }
            listInProgress = cardsOnTop + otherCards
        }

        return listInProgress
    }


}

data class SortingSetting(
    val ascending: Boolean = true,
    val showOwnOnTop: Boolean = true,
    val separateTypes: Boolean = true,
    val separateAuthors: Boolean = true
)
