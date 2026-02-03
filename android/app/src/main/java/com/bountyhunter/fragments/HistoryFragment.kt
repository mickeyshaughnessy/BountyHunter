package com.bountyhunter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bountyhunter.R
import com.bountyhunter.adapters.WorkoutAdapter
import com.bountyhunter.api.RetrofitClient
import com.bountyhunter.utils.PreferenceManager
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: WorkoutAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferenceManager = PreferenceManager(requireContext())
        
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WorkoutAdapter()
        recyclerView.adapter = adapter
        
        loadHistory()
    }
    
    fun refresh() {
        loadHistory()
    }
    
    private fun loadHistory() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                val token = preferenceManager.getBearerToken() ?: return@launch
                val response = RetrofitClient.apiService.getWorkoutHistory(token)
                
                progressBar.visibility = View.GONE
                
                if (response.isSuccessful) {
                    val workouts = response.body() ?: emptyList()
                    if (workouts.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        adapter.submitList(workouts.reversed())
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
