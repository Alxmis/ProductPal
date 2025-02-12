package com.example.myapplication.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentPage1Binding
import com.example.myapplication.database.Product
import com.example.myapplication.ui.ProductAdapter

class PageFragment1 : Fragment() {
    private lateinit var saveButton: Button
    private lateinit var productNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var productListRecyclerView: RecyclerView
    private val productList = mutableListOf<Product>()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPage1Binding.inflate(inflater, container, false)

        saveButton = binding.saveButton
        productNameEditText = binding.productNameEditText
        quantityEditText = binding.quantityEditText
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

        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                validateInput()
            }
        })

        saveButton.setOnClickListener {
            val productName = productNameEditText.text.toString()
            val quantity = quantityEditText.text.toString().toIntOrNull()
            val price = 0.0

            if (productName.isNotBlank() && quantity != null) {
                val product = Product(productName, quantity, price)
                addProduct(product)
                saveButton.text = "Сохранено"
            }
        }

        return binding.root
    }

    private fun validateInput() {
        val isInputValid = productNameEditText.text.isNotBlank() && quantityEditText.text.isNotBlank()
        saveButton.isEnabled = isInputValid
    }

    private fun addProduct(product: Product) {
        productAdapter.addProduct(product)
    }

    private fun removeProduct(product: Product) {
        productAdapter.removeProduct(product)
    }
}