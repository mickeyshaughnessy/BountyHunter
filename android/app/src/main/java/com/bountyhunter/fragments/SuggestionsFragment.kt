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
import com.bountyhunter.adapters.SuggestionsAdapter
import com.bountyhunter.api.RetrofitClient
import com.bountyhunter.utils.PreferenceManager
import kotlinx.coroutines.launch

class SuggestionsFragment : Fragment() {
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: SuggestionsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_suggestions, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferenceManager = PreferenceManager(requireContext())
        
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SuggestionsAdapter { suggestion ->
            // Navigate to log workout fragment with pre-filled data
            val logWorkoutFragment = parentFragmentManager.findFragmentByTag("f1") as? LogWorkoutFragment
            logWorkoutFragment?.fillFromSuggestion(suggestion.title, suggestion.type, suggestion.duration)
            
            // Switch to log workout tab
            (requireActivity() as? com.bountyhunter.DashboardActivity)?.let { activity ->
                view.post {
                    activity.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)?.currentItem = 1
                }
            }
        }
        recyclerView.adapter = adapter
        
        loadSuggestions()
    }
    
    private fun loadSuggestions() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                val token = preferenceManager.getBearerToken() ?: return@launch
                val response = RetrofitClient.apiService.getSuggestions(token)
                
                progressBar.visibility = View.GONE
                
                if (response.isSuccessful) {
                    val suggestions = response.body() ?: emptyList()
                    if (suggestions.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        adapter.submitList(suggestions)
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
