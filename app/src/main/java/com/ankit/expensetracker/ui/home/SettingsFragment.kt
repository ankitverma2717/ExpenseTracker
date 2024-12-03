package com.ankit.expensetracker.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ankit.expensetracker.R
import com.ankit.expensetracker.databinding.FragmentSettingsBinding
import com.ankit.expensetracker.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.signOutBtn.setOnClickListener {
            // Sign out the user from Firebase
            FirebaseAuth.getInstance().signOut()
            Intent(requireContext(), AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("SHOW_AUTH", true)
                startActivity(this)
            }
            requireActivity().finish()
        }

        return binding.root
    }

}