package com.ankit.expensetracker.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ankit.expensetracker.databinding.ExpenseItemLayoutBinding
import com.ankit.expensetracker.ui.models.TransactionModel

class TransactionAdapter (
    private val onClick: (TransactionModel) -> Unit):
    ListAdapter<TransactionModel,TransactionAdapter.TransactionViewHolder>(ExpenseDiffCallback) {

    class TransactionViewHolder(private val binding: ExpenseItemLayoutBinding, val onClick: (TransactionModel) -> Unit): RecyclerView.ViewHolder(binding.root) {

        private var currentTransaction: TransactionModel? = null

        init {
            binding.root.setOnClickListener {
                currentTransaction?.let { transaction ->
                    onClick(transaction)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(transactionModel: TransactionModel) {

            currentTransaction = transactionModel

            binding.tvDescription.text = transactionModel.description
            binding.tvExpenseNumber.text = "Expense#${transactionModel.transactionNumber}"
            binding.tvExpenseDate.text = transactionModel.date
            binding.tvTotalAmount.text = transactionModel.amount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(ExpenseItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

object ExpenseDiffCallback : DiffUtil.ItemCallback<TransactionModel>() {
    override fun areItemsTheSame(oldItem: TransactionModel, newItem: TransactionModel): Boolean {
        return oldItem.transactionNumber == newItem.transactionNumber
    }

    override fun areContentsTheSame(oldItem: TransactionModel, newItem: TransactionModel): Boolean {
        return oldItem == newItem
    }
}