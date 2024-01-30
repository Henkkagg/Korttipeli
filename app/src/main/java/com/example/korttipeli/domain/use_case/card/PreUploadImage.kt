package com.example.korttipeli.domain.use_case.card

import com.example.korttipeli.domain.repository.CardsRepository
import com.example.korttipeli.domain.use_case.CardResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreUploadImage @Inject constructor(private val repository: CardsRepository) {

    suspend operator fun invoke(base64Image: String): CardResult {

        return withContext(Dispatchers.IO) {
            repository.preUploadImage(base64Image)
        }
    }
}