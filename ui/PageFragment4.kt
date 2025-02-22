package com.example.myapplication.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.myapplication.database.Product
import com.example.myapplication.databinding.FragmentPage4Binding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Locale

class PageFragment4 : Fragment(), FontSizeUpdatable {

    private lateinit var productNameEditText: EditText
    private lateinit var expirationDateEditText: EditText
    private lateinit var caloriesEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var saveProductButton: Button
    private lateinit var scannerButton: Button

    private lateinit var productViewModel: Page3ProductViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPage4Binding.inflate(inflater, container, false)

        productViewModel = ViewModelProvider(requireActivity()).get(Page3ProductViewModel::class.java)

        productNameEditText = binding.productNameEditText
        expirationDateEditText = binding.expirationDateEditText
        caloriesEditText = binding.caloriesEditText
        quantityEditText = binding.quantityEditText
        saveProductButton = binding.saveProductButton
        scannerButton = binding.scannerButton

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        productNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        expirationDateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                validateInput()

                val dateText = editable.toString()
                if (dateText.length == 2 || dateText.length == 5) {
                    expirationDateEditText.append(".")
                }
            }
        })

        caloriesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        saveProductButton.setOnClickListener {
            saveProduct()
        }

        scannerButton.setOnClickListener {
            startScanner()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        applyFontSize()
    }

    override fun applyFontSize() {
        val fontSize = sharedPreferences.getInt("font_size", 16).toFloat()
        val rootView: ViewGroup = requireView() as ViewGroup
        updateFontSizeInViewGroup(rootView, fontSize)
    }

    private fun updateFontSizeInViewGroup(viewGroup: ViewGroup, fontSize: Float) {
        for (i in 0 until viewGroup.childCount) {
            val childView = viewGroup.getChildAt(i)
            when (childView) {
                is EditText -> childView.textSize = fontSize
                is Button -> childView.textSize = fontSize
                is TextView -> childView.textSize = fontSize
            }
            if (childView is ViewGroup) {
                updateFontSizeInViewGroup(childView, fontSize)
            }
        }
    }

    private fun validateInput() {
        val isValid = productNameEditText.text.isNotBlank() &&
                expirationDateEditText.text.isNotBlank()

        saveProductButton.isEnabled = isValid
    }

    private fun saveProduct() {
        val name = productNameEditText.text.toString()
        val expirationDateStr = expirationDateEditText.text.toString()
        val calories = caloriesEditText.text.toString().toIntOrNull()
        val quantity = quantityEditText.text.toString().toIntOrNull()

        if (name.isBlank() || expirationDateStr.isBlank()) {
            Toast.makeText(requireContext(), "Ошибка: все обязательные поля должны быть заполнены", Toast.LENGTH_SHORT).show()
            return
        }

        val expirationDate = parseExpirationDate(expirationDateStr)
        if (expirationDate == null) {
            Toast.makeText(requireContext(), "Ошибка: неверный формат даты", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(name = name, expirationDate = expirationDate, calories = calories ?: 0, quantity = quantity ?: 0)
        productViewModel.addProduct(product)

        Toast.makeText(requireContext(), "Продукт сохранен!", Toast.LENGTH_SHORT).show()
    }

    private fun parseExpirationDate(dateString: String): Long? {
        return try {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = format.parse(dateString)
            date?.time
        } catch (e: Exception) {
            null
        }
    }

    private fun startScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setPrompt("Сканируйте штрихкод")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Сканирование отменено", Toast.LENGTH_SHORT).show()
            } else {
                val scannedProductInfo = result.contents.split(";")
                if (scannedProductInfo.size >= 2) {
                    productNameEditText.setText(scannedProductInfo[0])
                    expirationDateEditText.setText(scannedProductInfo[1])
                } else {
                    Toast.makeText(requireContext(), "Ошибка: неверный штрихкод", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}