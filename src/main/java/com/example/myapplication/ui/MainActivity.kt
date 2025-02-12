package com.example.myapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.content.Intent
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager2: ViewPager2
    private lateinit var fragmentAdapter: FragmentStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        viewPager2 = findViewById(R.id.dashboard_fragment_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        fragmentAdapter = MyFragmentStateAdapter(this)
        viewPager2.adapter = fragmentAdapter

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_page1 -> {
                    viewPager2.currentItem = 0
                    true
                }
                R.id.nav_page2 -> {
                    viewPager2.currentItem = 1
                    true
                }
                R.id.nav_page3 -> {
                    viewPager2.currentItem = 2
                    true
                }
                R.id.nav_page4 -> {
                    viewPager2.currentItem = 3
                    true
                }
                else -> false
            }
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.nav_page1
                    1 -> R.id.nav_page2
                    2 -> R.id.nav_page3
                    3 -> R.id.nav_page4
                    else -> throw IllegalStateException("Unexpected position $position")
                }
            }
        })

        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}