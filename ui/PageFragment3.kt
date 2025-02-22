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
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PageFragment3 : Fragment(), FontSizeUpdatable {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productListRecyclerView: RecyclerView
    private lateinit var totalCaloriesTextView: TextView
    private lateinit var productViewModel: Page3ProductViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPage3Binding.inflate(inflater, container, false)

        productListRecyclerView = binding.productListRecyclerView
        totalCaloriesTextView = binding.totalCaloriesTextView

        productViewModel = ViewModelProvider(requireActivity()).get(Page3ProductViewModel::class.java)

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyFontSize()
    }

    override fun onResume() {
        super.onResume()
        productAdapter.resetDailyFields()
    }

    override fun applyFontSize() {
        val fontSize = sharedPreferences.getInt("font_size", 16).toFloat()
        updateFontSizeInViewGroup(requireView() as ViewGroup, fontSize)
    }

    private fun updateFontSizeInViewGroup(viewGroup: ViewGroup, fontSize: Float) {
        for (i in 0 until viewGroup.childCount) {
            val childView = viewGroup.getChildAt(i)

            when (childView) {
                is TextView -> childView.textSize = fontSize
            }

            if (childView is ViewGroup) {
                updateFontSizeInViewGroup(childView, fontSize)
            }
        }
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
        productAdapter.notifyDataSetChanged()
    }

    private fun decrementProductQuantity(product: Product) {
        if (product.quantity > 1) {
            product.quantity -= 1
            productViewModel.updateProductQuantity(product, product.quantity)
            productViewModel.addCaloriesToTotal(product.calories)
        }
        updateTotalCalories(productViewModel.productList.value ?: listOf())
        productAdapter.notifyDataSetChanged()
    }

    private fun updateTotalCalories(productList: List<Product>) {
        var totalCalories = 0
        productList.forEach {
            if (it.isChecked) {
                val productCalories = it.calories
                val productQuantity = it.quantity
                totalCalories += productCalories * productQuantity
            }
        }
        totalCaloriesTextView.text = "Итого калорий: $totalCalories"
    }
}