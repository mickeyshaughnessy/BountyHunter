package com.bountyhunter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bountyhunter.api.RetrofitClient
import com.bountyhunter.models.LoginRequest
import com.bountyhunter.utils.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        preferenceManager = PreferenceManager(this)
        
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoToRegister = findViewById<Button>(R.id.btnGoToRegister)
        
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            login(username, password)
        }
        
        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
    
    private fun login(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(username, password))
                
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        preferenceManager.saveAuthToken(loginResponse.token)
                        preferenceManager.saveUser(
                            loginResponse.user.id,
                            loginResponse.user.username,
                            loginResponse.user.email
                        )
                        
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
