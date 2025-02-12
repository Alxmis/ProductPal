package com.example.myapplication.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.myapplication.R
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var fontSizeSeekBar: SeekBar
    private lateinit var themeSwitch: Switch
    private lateinit var russianRadioButton: RadioButton
    private lateinit var englishRadioButton: RadioButton
    private lateinit var techSupportButton: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Font Size Adjustment
        fontSizeSeekBar = findViewById(R.id.fontSizeSeekBar)
        val savedFontSize = sharedPreferences.getInt("font_size", 14)
        fontSizeSeekBar.progress = savedFontSize
        fontSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sharedPreferences.edit().putInt("font_size", progress).apply()
                Toast.makeText(this@SettingsActivity, "Font size updated", Toast.LENGTH_SHORT).show()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Language Selection
        russianRadioButton = findViewById(R.id.russianRadioButton)
        englishRadioButton = findViewById(R.id.englishRadioButton)
        val language = sharedPreferences.getString("language", "ru")
        if (language == "ru") {
            russianRadioButton.isChecked = true
        } else {
            englishRadioButton.isChecked = true
        }

        russianRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setLanguage("ru")
            }
        }
        englishRadioButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setLanguage("en")
            }
        }

        // Tech Support Link
        techSupportButton = findViewById(R.id.techSupportButton)
        techSupportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/your_telegram_link"))
            startActivity(intent)
        }

        // Theme Toggle (Dark/Light Mode)
        themeSwitch = findViewById(R.id.themeSwitch)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        themeSwitch.isChecked = isDarkMode
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferences.edit().putBoolean("dark_mode", true).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferences.edit().putBoolean("dark_mode", false).apply()
            }
        }
    }

    private fun setLanguage(language: String) {
        val locale = if (language == "ru") {
            Locale("ru")
        } else {
            Locale("en")
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        sharedPreferences.edit().putString("language", language).apply()
    }
}