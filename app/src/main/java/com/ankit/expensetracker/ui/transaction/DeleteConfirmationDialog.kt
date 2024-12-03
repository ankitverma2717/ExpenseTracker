package com.ankit.expensetracker.ui.transaction

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DeleteConfirmationDialog : DialogFragment() {
    private var onConfirmClick: (() -> Unit)? = null

    fun setOnConfirmClickListener(listener: () -> Unit) {
        onConfirmClick = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete") { _, _ ->
                onConfirmClick?.invoke()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    companion object {
        const val TAG = "DeleteConfirmationDialog"
    }
}