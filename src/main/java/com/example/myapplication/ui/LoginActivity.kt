package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.UserDao
import com.example.myapplication.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userDao: UserDao
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("LoginActivity", "onCreate called")

        // Initialize the database
        val db = AppDatabase.getInstance(this)
        userDao = db.userDao()
        Log.d("LoginActivity", "Database initialized")

        // Toggle theme between dark and light
        binding.themeSwitch.setOnClickListener {
            isDarkMode = !isDarkMode
            setTheme(if (isDarkMode) android.R.style.ThemeOverlay_Material_Dark else android.R.style.ThemeOverlay_Material_Light)
            recreate()
            Log.d("LoginActivity", "Theme changed to dark mode: $isDarkMode")
        }

        // Handle the login logic
        binding.loginButton.setOnClickListener {
            val login = "@" + binding.login.text.toString().trim()
            val password = binding.password.text.toString().trim()

            Log.d("LoginActivity", "Login attempt with login: $login")

            // Ensure fields are not empty
            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity", "Login or password is empty")
                return@setOnClickListener
            }

            // Handle admin login
            if (login == "@admin" && password == "admin123") {
                Log.d("LoginActivity", "Admin login successful")
                Toast.makeText(this, "Добро пожаловать, Администратор!", Toast.LENGTH_SHORT).show()
                // Redirect to MainActivity for admin
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()  // Close the login screen after successful login
            } else {
                Log.d("LoginActivity", "Regular user login attempt")
                // Regular user login
                lifecycleScope.launch(Dispatchers.IO) {
                    Log.d("LoginActivity", "Attempting to authenticate user in background")
                    val user = userDao.authenticate(login, password)
                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            Log.d("LoginActivity", "User authenticated successfully")
                            Toast.makeText(this@LoginActivity, "Вход успешен!", Toast.LENGTH_SHORT).show()
                            binding.loginButton.text = "Вошел"
                            binding.loginButton.isEnabled = false
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Log.d("LoginActivity", "User authentication failed")
                            Toast.makeText(this@LoginActivity, "Ошибка: неправильные данные!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}