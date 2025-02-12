package com.example.myapplication.ui

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.database.User
import com.example.myapplication.databinding.FragmentRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getInstance(requireContext())
        val userDao = db.userDao()

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
            val login = "@" + binding.login.text.toString().trim()
            val birthYear = binding.birthYear.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (login.isEmpty() || birthYear.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val existingUser = userDao.getUserByLogin(login)
                if (existingUser != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Пользователь уже существует!", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val user = User(login = login, name = birthYear, password = password)
                userDao.insert(user)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Вы успешно зарегистрированы! Теперь войдите.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
