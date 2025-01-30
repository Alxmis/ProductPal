package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.UserDao
import com.example.myapplication.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val db = AppDatabase.getInstance(this)
            userDao = db.userDao()
        } catch (e: Exception) {
            Log.e("LoginActivity", "Database initialization failed: ${e.message}")
            Toast.makeText(this, "Ошибка инициализации БД!", Toast.LENGTH_LONG).show()
            return
        }

        binding.loginButton.setOnClickListener {
            val login = binding.login.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    Log.d("LoginActivity", "Authenticating user: $login")
                    val user = userDao.authenticate(login, password)

                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            Log.d("LoginActivity", "Login successful: ${user.name}")
                            Toast.makeText(this@LoginActivity, "Вход успешен!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Log.w("LoginActivity", "Authentication failed for: $login")
                            Toast.makeText(this@LoginActivity, "Ошибка: неправильные данные!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("LoginActivity", "Error during authentication: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Ошибка входа!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}