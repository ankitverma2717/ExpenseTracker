package com.ankit.expensetracker.ui.models

import kotlinx.serialization.Serializable

@Serializable
data class TransactionModel(
    val transactionNumber: Int,
    val transactionType: String,
    val description: String,
    val date: String,
    val amount: Double
)