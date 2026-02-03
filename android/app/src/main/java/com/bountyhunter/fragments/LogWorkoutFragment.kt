package com.bountyhunter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bountyhunter.R
import com.bountyhunter.api.RetrofitClient
import com.bountyhunter.models.LogWorkoutRequest
import com.bountyhunter.models.WorkoutData
import com.bountyhunter.utils.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LogWorkoutFragment : Fragment() {
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var etTitle: TextInputEditText
    private lateinit var actvType: AutoCompleteTextView
    private lateinit var etDuration: TextInputEditText
    private lateinit var etNotes: TextInputEditText
    private lateinit var btnSave: Button
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log_workout, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferenceManager = PreferenceManager(requireContext())
        
        etTitle = view.findViewById(R.id.etTitle)
        actvType = view.findViewById(R.id.actvType)
        etDuration = view.findViewById(R.id.etDuration)
        etNotes = view.findViewById(R.id.etNotes)
        btnSave = view.findViewById(R.id.btnSave)
        
        val types = arrayOf("individual", "group", "gym", "sports", "free")
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types)
        actvType.setAdapter(typeAdapter)
        
        btnSave.setOnClickListener {
            saveWorkout()
        }
    }
    
    fun fillFromSuggestion(title: String, type: String, duration: String) {
        etTitle.setText(title)
        actvType.setText(type, false)
        etDuration.setText(duration)
    }
    
    private fun saveWorkout() {
        val title = etTitle.text.toString().trim()
        val type = actvType.text.toString().trim()
        val duration = etDuration.text.toString().trim()
        val notes = etNotes.text.toString().trim()
        
        if (title.isEmpty() || type.isEmpty() || duration.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                val token = preferenceManager.getBearerToken() ?: return@launch
                val request = LogWorkoutRequest(
                    title = title,
                    type = type,
                    duration = duration,
                    data = WorkoutData(notes.ifEmpty { null })
                )
                
                val response = RetrofitClient.apiService.logWorkout(token, request)
                
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Workout logged!", Toast.LENGTH_SHORT).show()
                    clearForm()
                    
                    // Refresh history tab
                    val historyFragment = parentFragmentManager.findFragmentByTag("f2") as? HistoryFragment
                    historyFragment?.refresh()
                } else {
                    Toast.makeText(requireContext(), "Failed to log workout", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun clearForm() {
        etTitle.text?.clear()
        actvType.text?.clear()
        etDuration.text?.clear()
        etNotes.text?.clear()
    }
}
