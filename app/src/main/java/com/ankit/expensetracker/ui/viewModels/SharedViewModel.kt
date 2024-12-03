package com.ankit.expensetracker.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ankit.expensetracker.data.remote.Resource
import com.ankit.expensetracker.data.repository.FirestoreRepository
import com.ankit.expensetracker.ui.models.TransactionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<Resource<List<TransactionModel>>>(Resource.Loading())
//    val transactions: StateFlow<Resource<List<TransactionModel>>> = _transactions.asStateFlow()

    private val _transactionList = MutableStateFlow<List<TransactionModel>>(emptyList())
    val transactionList = _transactionList.asStateFlow()

    init {
        loadTransactions()
    }

    fun addTransaction(description: String, date: String, amount: Double, transactionType: String) {
        viewModelScope.launch {
            val transaction = TransactionModel(
                transactionNumber = 0,
                description = description,
                date = date,
                amount = amount,
                transactionType = transactionType
            )
            val result = repository.addTransaction(transaction)
            if (result is Resource.Success) {
                loadTransactions()
            }
        }
    }

    fun deleteTransaction(transactionNumber: Int) {
        viewModelScope.launch {
            val result = repository.deleteTransaction(transactionNumber)
            if (result is Resource.Success) {
                // Immediately remove the item from the local list
                _transactionList.update { currentList ->
                    currentList.filter { it.transactionNumber != transactionNumber }
                }
                // Reload the transactions from repository to ensure sync
                loadTransactions()
            }
        }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            try {
                Log.d("SharedViewModel", "Starting to load transactions")
                when (val result = repository.getTransactions()) {
                    is Resource.Success -> {
                        result.data?.let { transactions ->
                            Log.d("SharedViewModel", "Successfully loaded ${transactions.size} transactions")
                            _transactionList.emit(transactions)
                        }
                    }
                    is Resource.Error -> {
                        Log.e("SharedViewModel", "Error loading transactions: ${result.message}")
                        // Handle error - maybe show a message to the user
                    }
                    is Resource.Loading -> {
                        Log.d("SharedViewModel", "Loading transactions...")
                    }
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Exception in loadTransactions: ${e.message}", e)
            }
        }
    }

    fun updateTransaction(transaction: TransactionModel) {
        viewModelScope.launch {
            val result = repository.updateTransaction(transaction)
            if (result is Resource.Success) {
                loadTransactions()
            }
        }
    }

    suspend fun setInitialData(transactions: List<TransactionModel>) {
        if (_transactionList.value.isEmpty()) {
            _transactionList.emit(transactions)
            Log.d("SharedViewModel", "Initial data set, size: ${transactions.size}")
        } else {
            Log.d("SharedViewModel", "Skipping initial data set, current size: ${_transactionList.value.size}")
        }
    }

    fun getCurrentListSize(): Int = _transactionList.value.size
}