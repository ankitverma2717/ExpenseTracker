package com.ankit.expensetracker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.ankit.expensetracker.R
import com.ankit.expensetracker.ui.viewModels.SharedViewModel
import com.ankit.expensetracker.databinding.FragmentHomeBinding
import com.ankit.expensetracker.ui.adapter.TransactionAdapter
import com.ankit.expensetracker.ui.models.TransactionModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private val sharedViewModel: SharedViewModel by viewModels()

    private var isDataInitialized = false

    private val scope = lifecycleScope

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeTransactionList()

        if (!isDataInitialized) {
            loadDummyData()
            isDataInitialized = true
        }

        binding.addTransBtn.setOnClickListener {
            // Navigate to AddTransactionFragment
            findNavController().navigate(R.id.addTransactionFragment)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onClick = { transaction ->
                val transactionJson = Json.encodeToString(transaction)
                val bundle = Bundle().apply {
                    putString("transaction", transactionJson)
                }
                findNavController().navigate(
                    R.id.editTransactionFragment,
                    bundle
                )
            }
        )

        binding.mainRcv.apply {
            adapter = transactionAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeTransactionList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.transactionList.collect { transactions ->
                    Log.d("HomeFragment", "Received updated list, size: ${transactions.size}")
                    transactionAdapter.submitList(transactions.toList())
                    binding.mainRcv.scrollToPosition(0) // Optional: scroll to top after update
                }
            }
        }
    }

    private fun loadDummyData() {
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("HomeFragment", "Loading dummy data...")
            val dummyTransactions = listOf(
                TransactionModel(
                    transactionNumber = 1,
                    description = "Electricity Bill",
                    amount = 1000.00,
                    date = "15 Oct 2024",
                    transactionType = "Income"
                ),
                TransactionModel(
                    transactionNumber = 2,
                    description = "Groceries",
                    amount = 500.00,
                    date = "16 Oct 2024",
                    transactionType = "expense"
                ),
                TransactionModel(
                    transactionNumber = 3,
                    description = "House Rent",
                    amount = 1800.00,
                    date = "17 Nov 2024",
                    transactionType = "Income"
                ),
                TransactionModel(
                    transactionNumber = 4,
                    description = "Electricity Bill",
                    amount = 1000.00,
                    date = "15 Oct 2024",
                    transactionType = "Income"
                ),
            )
            sharedViewModel.setInitialData(dummyTransactions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
    }

}