package com.bountyhunter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bountyhunter.api.RetrofitClient
import com.bountyhunter.models.LoginRequest
import com.bountyhunter.models.RegisterRequest
import com.bountyhunter.utils.PreferenceManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        preferenceManager = PreferenceManager(this)
        
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etFullName = findViewById<TextInputEditText>(R.id.etFullName)
        val etLocation = findViewById<TextInputEditText>(R.id.etLocation)
        val etAge = findViewById<TextInputEditText>(R.id.etAge)
        val etWeight = findViewById<TextInputEditText>(R.id.etWeight)
        val chipGroupActivities = findViewById<ChipGroup>(R.id.chipGroupActivities)
        val etPaymentInfo = findViewById<TextInputEditText>(R.id.etPaymentInfo)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoToLogin = findViewById<Button>(R.id.btnGoToLogin)
        
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val activityTypes = mutableListOf<String>()
            for (i in 0 until chipGroupActivities.childCount) {
                val chip = chipGroupActivities.getChildAt(i) as Chip
                if (chip.isChecked) {
                    activityTypes.add(chip.text.toString().lowercase())
                }
            }
            
            val request = RegisterRequest(
                username = username,
                email = email,
                password = password,
                fullName = etFullName.text.toString().trim().ifEmpty { null },
                location = etLocation.text.toString().trim().ifEmpty { null },
                age = etAge.text.toString().trim().toIntOrNull(),
                weight = etWeight.text.toString().trim().toIntOrNull(),
                activityTypes = activityTypes,
                paymentInfo = etPaymentInfo.text.toString().trim().ifEmpty { null }
            )
            
            register(request, password)
        }
        
        btnGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun register(request: RegisterRequest, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.register(request)
                
                if (response.isSuccessful) {
                    // Auto-login after successful registration
                    autoLogin(request.username, password)
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun autoLogin(username: String, password: String) {
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
                        
                        startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                        finish()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Registered! Please login.",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
