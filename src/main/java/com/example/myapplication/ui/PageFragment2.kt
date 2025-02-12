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
import com.example.myapplication.databinding.FragmentPage2Binding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import android.content.Intent

class PageFragment2 : Fragment() {
    private lateinit var saveButton: Button
    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var scannerButton: Button
    private lateinit var addProductButton: Button
    private lateinit var totalPriceTextView: TextView
    private lateinit var productListRecyclerView: RecyclerView
    private val productList = mutableListOf<Product>()
    private lateinit var productAdapter: ProductAdapter
    private var totalPrice = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPage2Binding.inflate(inflater, container, false)

        productNameEditText = binding.productNameEditText
        productPriceEditText = binding.productPriceEditText
        scannerButton = binding.scannerButton
        addProductButton = binding.addProductButton
        totalPriceTextView = binding.totalPriceTextView
        productListRecyclerView = binding.productListRecyclerView

        productAdapter = ProductAdapter(productList) { product ->
            removeProduct(product)
        }
        productListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productListRecyclerView.adapter = productAdapter

        productNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        productPriceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        // Setup scanner button
        scannerButton.setOnClickListener {
            startScanner()
        }

        // Setup add product button
        addProductButton.setOnClickListener {
            val productName = productNameEditText.text.toString()
            val price = productPriceEditText.text.toString().toDoubleOrNull()
            val quantity = 1

            if (price != null) {
                val product = Product(productName, quantity, price)
                addProduct(product)
            } else {
                Toast.makeText(requireContext(), "Ошибка: неверная цена", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun validateInput() {
        val isInputValid = productPriceEditText.text.isNotBlank()
        addProductButton.isEnabled = isInputValid
    }

    private fun addProduct(product: Product) {
        productAdapter.addProduct(product)
        totalPrice += product.price.toDouble()
        totalPriceTextView.text = "Итоговая стоимость: $totalPrice руб"
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
                val price = result.contents.toDoubleOrNull()
                if (price != null) {
                    productPriceEditText.setText(price.toString())
                } else {
                    Toast.makeText(requireContext(), "Ошибка: неверный штрихкод", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun removeProduct(product: Product) {
        productAdapter.removeProduct(product)
        totalPrice -= product.price
        totalPriceTextView.text = "Итоговая стоимость: $totalPrice руб"
    }
}