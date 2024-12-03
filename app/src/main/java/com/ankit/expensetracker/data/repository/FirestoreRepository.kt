package com.ankit.expensetracker.data.repository

import com.ankit.expensetracker.ui.models.TransactionModel
import com.ankit.expensetracker.data.remote.Resource

interface FirestoreRepository {

    suspend fun addTransaction(transaction: TransactionModel): Resource<Boolean>
    suspend fun getTransactions(): Resource<List<TransactionModel>>
    suspend fun updateTransaction(transaction: TransactionModel): Resource<Boolean>
    suspend fun deleteTransaction(transactionNumber: Int): Resource<Boolean>
    suspend fun getTransactionByNumber(transactionNumber: Int): Resource<TransactionModel?>

}