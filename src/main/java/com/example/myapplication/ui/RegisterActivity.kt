package com.example.myapplication.ui

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.User
import com.example.myapplication.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var isDarkMode = false
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getInstance(this)
        val userDao = db.userDao()

        // Переключение темы
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            isDarkMode = isChecked
            setTheme(if (isDarkMode) android.R.style.ThemeOverlay_Material_Dark else android.R.style.ThemeOverlay_Material_Light)
            recreate()
        }

        // Добавление "@" к логину
        binding.login.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val loginText = binding.login.text.toString().trim()
                if (loginText.isNotEmpty() && !loginText.startsWith("@")) {
                    binding.login.setText("@$loginText")
                }
            }
        }

        // Просмотр пароля (переключение значка глаза)
        binding.showPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            binding.password.inputType = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.password.setSelection(binding.password.text.length)
        }

        binding.registerButton.setOnClickListener {
            val login = binding.login.text.toString().trim()
            val birthYear = binding.birthYear.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (login.isEmpty() || birthYear.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val existingUser = userDao.getUserByLogin(login)
                if (existingUser != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Пользователь уже существует!", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val user = User(login = login, name = birthYear, password = password)
                userDao.insert(user)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Вы успешно зарегистрированы! Теперь войдите.", Toast.LENGTH_SHORT).show()
                    binding.registerButton.text = "Зарегистрирован"
                    binding.registerButton.isEnabled = false
                }
            }
        }
    }
}
