package com.example.korttipeli.presentation.main_menu.cards.card_inspector

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.data.SharedPref
import com.example.korttipeli.domain.model.Card
import com.example.korttipeli.domain.model.CardInfo
import com.example.korttipeli.domain.use_case.CardResult
import com.example.korttipeli.domain.use_case.CardUsecases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardInspectorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cardUsecases: CardUsecases,
    private val sharedPref: SharedPref
) : ViewModel() {
    //Card id is "" if creating a new card. Otherwise it contains the id
    private var cardId = ""
    var cardState: CardState by mutableStateOf(CardState())
        private set

    var inspectorMode: InspectorMode by mutableStateOf(InspectorMode.DuringGame)
        private set
    var shouldShowFab: Boolean by mutableStateOf(false)
        private set
    var shouldShowAlertDialog: Boolean by mutableStateOf(false)
        private set
    var shouldNavigateBack: Boolean by mutableStateOf(false)
        private set
    var resultMessage: String by mutableStateOf("")
        private set
    private var imageId = ""
    lateinit var editableCard: Card

    private lateinit var imageUploadJob: Job
    private lateinit var imageUploadResult: CardResult

    //New card has id of "". Existing cards have id's and are initialized from database
    fun onInit(
        cardId: String,
        openedDuringGame: Boolean,
        isUsingSecret: Boolean,
    ) {
        this.cardId = cardId

        if (cardId == "") {
            inspectorMode = InspectorMode.CreatingNew
            return
        }
        viewModelScope.launch {
            editableCard = cardUsecases.loadOne(cardId)
            val username = sharedPref.readUsername()
            cardState = CardState(
                cardType = editableCard.type,
                titleContent = editableCard.title,
                descriptionContent = editableCard.description,
                bitmap = BitmapFactory.decodeFile(editableCard.image),
                author = editableCard.author
            )

            //If card is inspected during the game, don't enable editing
            if (openedDuringGame) {
                inspectorMode = if (isUsingSecret) {
                    shouldShowFab = true
                    InspectorMode.UsingSecret
                } else {
                    InspectorMode.DuringGame
                }
                return@launch
            }

            if (editableCard.author == username) {
                inspectorMode = InspectorMode.InspectingOwn
                shouldShowFab = true
            } else inspectorMode = InspectorMode.InspectingFriends
        }
    }

    fun onEvent(event: CardInspectorEvent) {
        when (event) {
            is CardInspectorEvent.TitleTyped -> {
                cardState = cardState.copy(titleContent = event.value)
                showFabIfConditionsMet()
            }
            is CardInspectorEvent.DescriptionTyped -> {
                if (event.value.contains("\n")) return
                cardState = cardState.copy(descriptionContent = event.value)
                showFabIfConditionsMet()
            }
            is CardInspectorEvent.TypeChanged -> {
                cardState = cardState.copy(cardType = event.value)
                showFabIfConditionsMet()
            }
            is CardInspectorEvent.ImageChanged -> {
                cardState = cardState.copy(bitmap = cardUsecases.createBitmapFromUri(event.uri))
                imageUploadJob = viewModelScope.launch {
                    val base64Image = cardUsecases.convertBitmapToBase64(cardState.bitmap!!)
                    imageUploadResult = cardUsecases.preUploadImage(base64Image)

                    if (imageUploadResult !is CardResult.Success) {
                        cardState = cardState.copy(bitmap = null)
                        return@launch
                    }
                    imageId = (imageUploadResult as CardResult.Success).id
                    showFabIfConditionsMet()
                }
            }
            CardInspectorEvent.ConfirmPressed -> {
                if (cardId == "") createNew() else {
                    if (inspectorMode is InspectorMode.EditingOwn) {
                        inspectorMode = InspectorMode.PreviewingChanges
                        return
                    }
                    if (inspectorMode is InspectorMode.UsingSecret) {
                        shouldNavigateBack = true
                        return
                    }
                    updateExisting()
                }
            }

            CardInspectorEvent.CancelPressed -> {
                inspectorMode = if (cardId == "") {
                    InspectorMode.CreatingNew
                } else {
                    if (inspectorMode is InspectorMode.PreviewingChanges) {
                        InspectorMode.EditingOwn
                    } else InspectorMode.InspectingOwn
                }
            }
            CardInspectorEvent.EditPressed -> inspectorMode = InspectorMode.EditingOwn
            CardInspectorEvent.AlertDialogDismissed -> shouldShowAlertDialog = false
            CardInspectorEvent.DeletePressed -> shouldShowAlertDialog = true

            is CardInspectorEvent.AlertDialogConfirmed -> {
                viewModelScope.launch {
                    Log.i("apu", "Vahvistetttu")
                    val result = cardUsecases.deleteCard(cardId)
                    performActionBasedOnResult(result)
                }
            }
            CardInspectorEvent.PreviewPressed -> TODO()
        }
    }

    private fun showFabIfConditionsMet() {
        val nothingIsBlank =
            cardState.titleContent.isNotBlank() && cardState.descriptionContent.isNotBlank()

        //When creating a new card, a new image is required, but when updating a card, it's not
        shouldShowFab = if (cardId == "") {
            nothingIsBlank && imageId != ""
        } else nothingIsBlank
    }

    private fun createNew() {
        if (!this::imageUploadJob.isInitialized) return

        viewModelScope.launch {
            imageUploadJob.join()
            if (imageUploadResult !is CardResult.Success) {
                performActionBasedOnResult(imageUploadResult)
                return@launch
            }

            val cardInfo = CardInfo(
                title = cardState.titleContent,
                description = cardState.descriptionContent,
                type = cardState.cardType,
                imageId = imageId
            )
            val result = cardUsecases.createNew(cardInfo, cardState.bitmap!!)
            performActionBasedOnResult(result)
        }
    }

    private fun updateExisting() {
        viewModelScope.launch {
            val result = cardUsecases.update(
                CardInfo(
                    id = cardId,
                    title = cardState.titleContent,
                    description = cardState.descriptionContent,
                    type = cardState.cardType,
                    imageId = imageId
                ),
                if (imageId != "") cardState.bitmap else null
            )
            performActionBasedOnResult(result)
        }
    }

    private fun performActionBasedOnResult(result: CardResult) {

        when (result) {
            is CardResult.Success -> {
                resultMessage = "Onnistui"
                shouldNavigateBack = true
            }
            is CardResult.DescriptionTooLong -> {
                resultMessage =
                    "Kuvaus on ${result.attemptedLength - result.attemptedLength} merkkiä liian pitkä"
            }
            is CardResult.ImageTooLarge -> {
                resultMessage =
                    "Kuvan koko on ${result.attemptedSizeInKb - result.attemptedSizeInKb}kb liian suuri"
            }
            CardResult.ServerError -> {
                resultMessage = "Palvelinvirhe"
            }
            is CardResult.TitleAndDescriptionTooLong -> {
                resultMessage =
                    "Kortin otsikko on ${result.attemptedTitleLength - result.allowedTitleLength} merkkiä liian pitkä. " +
                            "Kortin kuvaus on ${result.attemptedDescriptionLength - result.allowedDescriptionLength} " +
                            "merkkiä liian pitkä"
            }
            is CardResult.TitleTooLong -> {
                resultMessage =
                    "Kortin otsikko on ${result.attemptedLength - result.allowedLength} merkkiä liian pitkä"
            }
            CardResult.ClientError -> {
                resultMessage =
                    "Kortin luonti onnistui, mutta sitä ei voitu tallentaa puhelimeesi. Tila lopussa?"
                shouldNavigateBack = true
            }
        }

    }

    fun navRequestAcknowledged() {
        shouldNavigateBack = false
    }

    fun resultMessageAcknowledged() {
        resultMessage = ""
    }
}

