package com.ankit.expensetracker.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.ankit.expensetracker.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Only set initial fragment if it's the first time the activity is created
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fcv, SplashFragment())
                .commit()
        }

        if(intent.getBooleanExtra("SHOW_AUTH", false)) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_fcv, AuthFragment())
                .commit()
        }
    }
}