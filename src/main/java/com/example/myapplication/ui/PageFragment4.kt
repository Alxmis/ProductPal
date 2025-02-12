package com.example.myapplication.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.Product
import com.example.myapplication.databinding.FragmentPage4Binding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Locale


class PageFragment4 : Fragment() {

    private lateinit var productNameEditText: EditText
    private lateinit var expirationDateEditText: EditText
    private lateinit var caloriesEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var saveProductButton: Button
    private lateinit var scannerButton: Button

    private var productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPage4Binding.inflate(inflater, container, false)

        productNameEditText = binding.productNameEditText
        expirationDateEditText = binding.expirationDateEditText
        caloriesEditText = binding.caloriesEditText
        quantityEditText = binding.quantityEditText
        saveProductButton = binding.saveProductButton
        scannerButton = binding.scannerButton

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

//        productNameEditText.addTextChangedListener { validateInput() }
//        expirationDateEditText.addTextChangedListener { validateInput() }
//        caloriesEditText.addTextChangedListener { validateInput() }
//        quantityEditText.addTextChangedListener { validateInput() }

        saveProductButton.setOnClickListener {
            saveProduct()
        }

        scannerButton.setOnClickListener {
            startScanner()
        }

        return binding.root
    }

    private fun validateInput() {
        val isValid = productNameEditText.text.isNotBlank() &&
                expirationDateEditText.text.isNotBlank() &&
                caloriesEditText.text.isNotBlank() &&
                quantityEditText.text.isNotBlank()

        saveProductButton.isEnabled = isValid

        if (!isValid) {
            if (productNameEditText.text.isBlank()) {
                productNameEditText.error = "Product name is required"
            }

            if (expirationDateEditText.text.isBlank()) {
                expirationDateEditText.error = "Expiration date is required"
            }

            if (caloriesEditText.text.isBlank()) {
                caloriesEditText.error = "Calories are required"
            }

            if (quantityEditText.text.isBlank()) {
                quantityEditText.error = "Quantity is required"
            }
        }
    }

    private fun saveProduct() {
        val name = productNameEditText.text.toString()
        val expirationDateStr = expirationDateEditText.text.toString()
        val calories = caloriesEditText.text.toString().toIntOrNull()
        val quantity = quantityEditText.text.toString().toIntOrNull()

        if (name.isBlank() || expirationDateStr.isBlank() || calories == null || quantity == null) {
            Toast.makeText(requireContext(), "Ошибка: все поля должны быть заполнены корректно", Toast.LENGTH_SHORT).show()
            return
        }

        val expirationDate = parseExpirationDate(expirationDateStr)
        if (expirationDate == null) {
            Toast.makeText(requireContext(), "Ошибка: неверный формат даты", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(name = name, expirationDate = expirationDate, calories = calories, quantity = quantity)
        productList.add(product)

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