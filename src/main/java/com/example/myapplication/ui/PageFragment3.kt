package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.Product
import com.example.myapplication.databinding.FragmentPage3Binding

class PageFragment3 : Fragment() {
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productListRecyclerView: RecyclerView
    private lateinit var totalCaloriesTextView: TextView
    private lateinit var productViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPage3Binding.inflate(inflater, container, false)

        productListRecyclerView = binding.productListRecyclerView
        totalCaloriesTextView = binding.totalCaloriesTextView

        productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)

        productAdapter = ProductAdapter(
            onDelete = { product -> removeProduct(product) },
            onCheck = { product -> toggleProductCheck(product) },
            onMinus = { product -> decrementProductQuantity(product) },
            layoutType = PAGE_TYPE_3
        )
        productListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        productListRecyclerView.adapter = productAdapter

        productViewModel.productList.observe(viewLifecycleOwner, { productList ->
            productAdapter.submitList(productList)
            productAdapter.notifyDataSetChanged()
            updateTotalCalories(productList)
        })

        return binding.root
    }

    private fun removeProduct(product: Product) {
        productViewModel.removeProduct(product)
    }

    private fun toggleProductCheck(product: Product) {
        if (product.isChecked) {
            productViewModel.removeCaloriesFromTotal(product.calories)
            product.isChecked = false
        } else {
            productViewModel.addCaloriesToTotal(product.calories)
            product.isChecked = true
        }
        updateTotalCalories(productViewModel.productList.value ?: listOf())
    }

    private fun decrementProductQuantity(product: Product) {
        if (product.quantity > 1) {
            product.quantity -= 1
            productViewModel.updateProductQuantity(product, product.quantity)
            productViewModel.addCaloriesToTotal(product.calories)
        }
        updateTotalCalories(productViewModel.productList.value ?: listOf())
    }

    private fun updateTotalCalories(productList: List<Product>) {
        var totalCalories = 0
        productList.forEach {
            if (it.isChecked) {
                totalCalories += it.calories
            }
        }
        totalCaloriesTextView.text = "Итого калорий: $totalCalories"
    }
}