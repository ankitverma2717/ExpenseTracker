package com.ankit.expensetracker.ui.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ankit.expensetracker.R
import com.ankit.expensetracker.ui.viewModels.SharedViewModel
import com.ankit.expensetracker.databinding.FragmentEditTransactionBinding
import com.ankit.expensetracker.ui.models.TransactionModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class EditTransactionFragment : Fragment() {

    private lateinit var binding: FragmentEditTransactionBinding
    private lateinit var currentTransaction: TransactionModel
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditTransactionBinding.inflate(inflater, container, false)

        arguments?.getString("transaction")?.let { jsonString ->
            currentTransaction = Json.decodeFromString(jsonString)
            populateTransactionData(currentTransaction)
        }

        setupToolbar()

        return binding.root
    }

    private fun populateTransactionData(transaction: TransactionModel) {
        binding.apply {
            tvExpenseNumber.text = "Expense#${transaction.transactionNumber}"
            tvExpenseDate.text = transaction.date
            tvTotalAmount.text = transaction.amount.toString()
            tvDescription.text = transaction.description
        }
    }

    private fun showDeleteConfirmationDialog() {
        val deleteDialog = DeleteConfirmationDialog().apply {
            setOnConfirmClickListener {
                if (::currentTransaction.isInitialized) {
                    Log.d("EditTransactionFragment", "Deleting transaction: ${currentTransaction.transactionNumber}")
                    sharedViewModel.deleteTransaction(currentTransaction.transactionNumber)
                    findNavController().navigateUp()
                }
            }
        }
        deleteDialog.show(parentFragmentManager, DeleteConfirmationDialog.TAG)
    }

    private fun setupToolbar() {

        binding.toolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolBar.inflateMenu(R.menu.edit_transaction_menu)
        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    showDeleteConfirmationDialog()
                    true
                }

                R.id.edit -> {
                    // Handle edit action
                    if (::currentTransaction.isInitialized) {
                        val transactionJson = Json.encodeToString(currentTransaction)
                        val bundle = Bundle().apply {
                            putString("transaction", transactionJson)
                            putBoolean("isEditing", true)
                        }
                        findNavController().navigate(
                            R.id.addTransactionFragment,
                            bundle
                        )
                    }
                    true
                }

                else -> false
            }
        }
    }

}