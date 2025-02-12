package com.example.myapplication.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentPage1Binding
import com.example.myapplication.database.Product
import com.example.myapplication.ui.ProductAdapter
import androidx.lifecycle.ViewModelProvider

class PageFragment1 : Fragment() {
    private lateinit var saveButton: Button
    private lateinit var productNameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var productListRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productViewModel: ProductViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPage1Binding.inflate(inflater, container, false)

        // Initialize the ViewModel
        productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)

        // Bind views
        saveButton = binding.saveButton
        productNameEditText = binding.productNameEditText
        quantityEditText = binding.quantityEditText
        productListRecyclerView = binding.productListRecyclerView

        productAdapter = ProductAdapter(
            onDelete = { product ->
                Log.d("PageFragment1", "Deleting product: ${product.name}")
                productViewModel.removeProduct(product)
            },
            onCheck = { product ->
                Log.d("PageFragment1", "Check action on product: ${product.name}")
                handleCheck(product)
            },
            onMinus = { product ->
                Log.d("PageFragment1", "Minus action on product: ${product.name}")
                handleMinus(product)
            },
            layoutType = PAGE_TYPE_1
        )
        productListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productListRecyclerView.adapter = productAdapter

        productViewModel.productList.observe(viewLifecycleOwner, { productList ->
            Log.d("PageFragment1", "Product list updated, size: ${productList.size}")
            productAdapter.submitList(productList)
            productAdapter.notifyDataSetChanged()
        })

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

            if (productName.isNotBlank() && quantity != null) {
                Log.d("PageFragment1", "Saving product: $productName with quantity: $quantity")
                val product = Product(name = productName, quantity = quantity, price = 0.0) // Example with 0 price
                productViewModel.addProduct(product)
                Log.d("PageFragment1", "Product added")
                saveButton.text = "Сохранено"
            } else {
                Log.d("PageFragment1", "Invalid input: productName or quantity is blank or incorrect")
                Toast.makeText(requireContext(), "Ошибка: неверные данные", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun validateInput() {
        val isInputValid = productNameEditText.text.isNotBlank() && quantityEditText.text.isNotBlank()
        saveButton.isEnabled = isInputValid
        Log.d("PageFragment1", "Validation status: $isInputValid")
    }

    private fun handleCheck(product: Product) {
        Log.d("PageFragment1", "Handle check for product: ${product.name}")
        productAdapter.notifyDataSetChanged()
    }

    private fun handleMinus(product: Product) {
        Log.d("PageFragment1", "Handle minus for product: ${product.name}")
        if (product.quantity > 1) {
            product.quantity -= 1
            productAdapter.notifyDataSetChanged()
        }
    }
}