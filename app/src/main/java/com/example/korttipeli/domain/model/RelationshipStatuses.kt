package com.example.korttipeli.domain.model

data class RelationshipStatuses(
    val friendsWith: List<String>,
    val sentRequests: List<String>,
    val receivedRequests: List<String>
)