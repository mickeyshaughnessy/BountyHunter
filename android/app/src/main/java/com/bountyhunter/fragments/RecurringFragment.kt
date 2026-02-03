package com.bountyhunter.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bountyhunter.R
import com.bountyhunter.adapters.RecurringAdapter
import com.bountyhunter.api.RetrofitClient
import com.bountyhunter.models.CreateRecurringRequest
import com.bountyhunter.utils.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RecurringFragment : Fragment() {
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var btnCreateRecurring: Button
    private lateinit var adapter: RecurringAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recurring, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferenceManager = PreferenceManager(requireContext())
        
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        btnCreateRecurring = view.findViewById(R.id.btnCreateRecurring)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecurringAdapter()
        recyclerView.adapter = adapter
        
        btnCreateRecurring.setOnClickListener {
            showCreateDialog()
        }
        
        loadRecurring()
    }
    
    private fun showCreateDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_create_recurring)
        
        val etTitle = dialog.findViewById<TextInputEditText>(R.id.etTitle)
        val actvSchedule = dialog.findViewById<AutoCompleteTextView>(R.id.actvSchedule)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val btnCreate = dialog.findViewById<Button>(R.id.btnCreate)
        
        val schedules = arrayOf("daily", "weekly", "biweekly", "monthly")
        val scheduleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, schedules)
        actvSchedule.setAdapter(scheduleAdapter)
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        btnCreate.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val schedule = actvSchedule.text.toString().trim()
            
            if (title.isEmpty() || schedule.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            createRecurring(title, schedule)
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun createRecurring(title: String, schedule: String) {
        lifecycleScope.launch {
            try {
                val token = preferenceManager.getBearerToken() ?: return@launch
                val request = CreateRecurringRequest(title, schedule)
                
                val response = RetrofitClient.apiService.createRecurringWorkout(token, request)
                
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Recurring workout created!", Toast.LENGTH_SHORT).show()
                    loadRecurring()
                } else {
                    Toast.makeText(requireContext(), "Failed to create", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadRecurring() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                val token = preferenceManager.getBearerToken() ?: return@launch
                val response = RetrofitClient.apiService.getRecurringWorkouts(token)
                
                progressBar.visibility = View.GONE
                
                if (response.isSuccessful) {
                    val recurring = response.body() ?: emptyList()
                    if (recurring.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        adapter.submitList(recurring)
                    }
                } else {
                    tvEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
