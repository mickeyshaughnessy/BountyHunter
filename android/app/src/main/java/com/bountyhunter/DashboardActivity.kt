package com.bountyhunter

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bountyhunter.fragments.HistoryFragment
import com.bountyhunter.fragments.LogWorkoutFragment
import com.bountyhunter.fragments.RecurringFragment
import com.bountyhunter.fragments.SuggestionsFragment
import com.bountyhunter.utils.PreferenceManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DashboardActivity : AppCompatActivity() {
    
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        
        preferenceManager = PreferenceManager(this)
        
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        
        setupViewPager()
    }
    
    private fun setupViewPager() {
        val adapter = DashboardPagerAdapter(this)
        viewPager.adapter = adapter
        
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.suggestions)
                1 -> getString(R.string.log_workout)
                2 -> getString(R.string.history)
                3 -> getString(R.string.recurring)
                else -> ""
            }
        }.attach()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        preferenceManager.clearAll()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    
    inner class DashboardPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SuggestionsFragment()
                1 -> LogWorkoutFragment()
                2 -> HistoryFragment()
                3 -> RecurringFragment()
                else -> SuggestionsFragment()
            }
        }
    }
}
