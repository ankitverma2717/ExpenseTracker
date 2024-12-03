package com.ankit.expensetracker.ui.transaction

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.ankit.expensetracker.R
import com.ankit.expensetracker.databinding.FragmentAddTransactionBinding
import com.ankit.expensetracker.ui.models.TransactionModel
import com.google.android.material.card.MaterialCardView
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionFragment : Fragment() {

    private lateinit var binding: FragmentAddTransactionBinding
    private var transactionToEdit: TransactionModel? = null
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        // Check if we're editing an existing transaction
        arguments?.let { args ->
            isEditing = args.getBoolean("isEditing", false)
            args.getString("transaction")?.let { jsonString ->
                transactionToEdit = Json.decodeFromString(jsonString)
                populateFields(transactionToEdit!!)
            }
        }

        setupRadioButtons()
        setUpToolBar()
        setupDatePicker()

        return binding.root
    }

    private fun setupDatePicker() {
        binding.tilDatePicker.editText?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)

                    // Format date as "01 Dec 2024"
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)

                    binding.tilDatePicker.editText?.setText(formattedDate)
                },
                year,
                month,
                day
            ).show()
        }
    }

    private fun setupRadioButtons() {
        // Create a RadioGroup for existing RadioButtons
        val radioGroup = RadioGroup(requireContext()).apply {
            id = View.generateViewId()
            orientation = RadioGroup.HORIZONTAL
        }

        // Get references to cards and radio buttons
        val expenseCard = binding.mcvRdExpense // Add this ID to your XML
        val incomeCard = binding.mcvRdIncome  // Add this ID to your XML

        // Set up RadioGroup listener
        binding.rdExpense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                expenseCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                incomeCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.text_color_tertiary)
                binding.rdIncome.isChecked = false
            }
        }

        binding.rdIncome.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                incomeCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                expenseCard.strokeColor = ContextCompat.getColor(requireContext(), R.color.text_color_tertiary)
                binding.rdExpense.isChecked = false
            }
        }

        // Set default selection
        binding.rdExpense.isChecked = true
    }

    private fun setUpToolBar() {
        binding.toolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun populateFields(transaction: TransactionModel) {
        binding.apply {
            // Populate your input fields with transaction data
            tilDescription.editText?.setText(transaction.description)
            tilTotalAmount.editText?.setText(transaction.amount.toString())
            tilDatePicker.editText?.setText(transaction.date)
            // Set other fields as needed
        }
    }

}