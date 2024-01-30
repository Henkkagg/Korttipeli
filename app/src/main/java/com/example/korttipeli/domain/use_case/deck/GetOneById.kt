package com.example.korttipeli.domain.use_case.deck

import com.example.korttipeli.data.room.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetOneById @Inject constructor(
    private val db: Database
) {

    suspend operator fun invoke(id: String) = withContext(Dispatchers.IO) {

        return@withContext db.decksDao().getOneById(id)
    }
}
