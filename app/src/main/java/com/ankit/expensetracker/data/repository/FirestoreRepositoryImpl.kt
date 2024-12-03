package com.ankit.expensetracker.data.repository

import android.util.Log
import com.ankit.expensetracker.data.remote.Resource
import com.ankit.expensetracker.ui.models.TransactionModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore
): FirestoreRepository {

    private val transactionsCollection = firestore.collection("transactions")

    override suspend fun addTransaction(transaction: TransactionModel): Resource<Boolean> {
        return try {
            val nextTransactionNumber = getNextTransactionNumber()
            val newTransaction = transaction.copy(transactionNumber = nextTransactionNumber)

            transactionsCollection
                .document(nextTransactionNumber.toString())
                .set(FirestoreSerializer.toMap(newTransaction))
                .await()

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getTransactions(): Resource<List<TransactionModel>> {
        return try {
            Log.d("Repository", "Fetching transactions...")
            val snapshot = transactionsCollection
                .orderBy("transactionNumber")
                .get()
                .await()

            val transactions = snapshot.documents.mapNotNull { document ->
                FirestoreSerializer.fromDocument(document)
            }

            Log.d("Repository", "Fetched ${transactions.size} transactions")
            Resource.Success(transactions)
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching transactions: ${e.message}", e)
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun updateTransaction(transaction: TransactionModel): Resource<Boolean> {
        return try {
            transactionsCollection
                .document(transaction.transactionNumber.toString())
                .set(FirestoreSerializer.toMap(transaction))
                .await()

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun deleteTransaction(transactionNumber: Int): Resource<Boolean> {
        return try {
            Log.d("Repository", "Deleting transaction: $transactionNumber")
            transactionsCollection
                .document(transactionNumber.toString())
                .delete()
                .await()
            Log.d("Repository", "Transaction deleted successfully")
            Resource.Success(true)
        } catch (e: Exception) {
            Log.e("Repository", "Error deleting transaction: ${e.message}")
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getTransactionByNumber(transactionNumber: Int): Resource<TransactionModel?> {
        return try {
            val document = transactionsCollection
                .document(transactionNumber.toString())
                .get()
                .await()

            val transaction = document.toObject(TransactionModel::class.java)
            Resource.Success(transaction)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    private suspend fun getNextTransactionNumber(): Int {
        val snapshot = transactionsCollection
            .orderBy("transactionNumber", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        return if (snapshot.isEmpty) {
            1
        } else {
            val lastTransaction = snapshot.documents[0].toObject(TransactionModel::class.java)
            (lastTransaction?.transactionNumber ?: 0) + 1
        }
    }
}

object FirestoreSerializer {
    fun fromDocument(document: DocumentSnapshot): TransactionModel? {
        return try {
            Log.d("Serializer", "Converting document: ${document.id}")
            val transactionNumber = document.getLong("transactionNumber")?.toInt()
            val description = document.getString("description")
            val date = document.getString("date")
            val transactionType = document.getString("transactionType")
            val amount = document.getDouble("amount")

            Log.d(
                "Serializer", """
                Transaction details:
                Number: $transactionNumber
                Description: $description
                Date: $date
                Type: $transactionType
                Amount: $amount
            """.trimIndent()
            )

            TransactionModel(
                transactionNumber = transactionNumber ?: 0,
                description = description ?: "",
                date = date ?: "",
                transactionType = transactionType ?: "",
                amount = amount ?: 0.0
            )
        } catch (e: Exception) {
            Log.e("Serializer", "Error converting document: ${e.message}", e)
            null
        }
    }

    fun toMap(transaction: TransactionModel): Map<String, Any> {
        return mapOf(
            "transactionNumber" to transaction.transactionNumber,
            "description" to transaction.description,
            "date" to transaction.date,
            "amount" to transaction.amount,
            "transactionType" to transaction.transactionType  // Make sure this is included
        )
    }

}