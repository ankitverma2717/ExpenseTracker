package com.ankit.expensetracker.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ankit.expensetracker.MainActivity
import com.ankit.expensetracker.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000L)

            val currentUser = auth.currentUser

            if (currentUser != null) {
                // User is logged in, navigate to MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                // Clear the current task and start a new one
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // User is not logged in, navigate to authentication fragment
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.auth_fcv, AuthFragment())
                    .commit()
            }

        }

    }

}