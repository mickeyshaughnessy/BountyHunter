package com.bountyhunter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bountyhunter.utils.PreferenceManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferenceManager = PreferenceManager(this)
        
        // Check if user is already logged in
        if (preferenceManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }
        
        setContentView(R.layout.activity_main)
        
        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    
    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
