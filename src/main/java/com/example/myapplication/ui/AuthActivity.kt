package com.example.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAuthBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.UserDao
import android.util.Log

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var adapter: AuthPagerAdapter
    private var isAdminTabAdded = false // Prevent multiple admin tabs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AuthPagerAdapter(this)
        binding.viewPager.adapter = adapter

        setupTabs() // Initial tab setup
    }

    private fun setupTabs() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Вход"
                1 -> "Регистрация"
                2 -> if (isAdminTabAdded) "Администрация" else ""
                else -> ""
            }
        }.attach()
    }

    fun addAdminTab() {
        if (!isAdminTabAdded) { // Prevent duplicate tabs
            Log.d("AuthActivity", "Adding Admin Tab")

            adapter.addAdminTab() // Modify adapter
            binding.viewPager.adapter = adapter // Refresh ViewPager2
            isAdminTabAdded = true

            // Reset TabLayout
            setupTabs()
        }
    }
}