package com.example.myapplication.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.appcompat.app.AppCompatActivity

class MyFragmentStateAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PageFragment1()
            1 -> PageFragment2()
            2 -> PageFragment3()
            3 -> PageFragment4()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}