package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemProductBinding
import com.example.myapplication.databinding.ItemProductPage1Binding
import com.example.myapplication.databinding.ItemProductPage2Binding
import com.example.myapplication.database.Product
import com.example.myapplication.R
import android.graphics.Color
import android.view.View
import androidx.viewbinding.ViewBinding

const val PAGE_TYPE_1 = 1
const val PAGE_TYPE_2 = 2
const val PAGE_TYPE_3 = 3

class ProductAdapter(
    private val onDelete: (Product) -> Unit,
    private val onCheck: (Product) -> Unit,
    private val onMinus: (Product) -> Unit,
    private val layoutType: Int,
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    private var totalCalories = 0

    class ProductViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, onDelete: (Product) -> Unit, onCheck: (Product) -> Unit, onMinus: (Product) -> Unit) {
            when (binding) {
                is ItemProductPage1Binding -> {
                    binding.productNameTextView.text = product.name
                    binding.quantityTextView.text = "${product.quantity} шт"

                    binding.deleteButton.setOnClickListener { onDelete(product) }
                }

                is ItemProductPage2Binding -> {
                    binding.productNameTextView.text = product.name
                    binding.priceTextView.text = "${product.price} руб"
                }

                is ItemProductBinding -> {
                    binding.productNameTextView.text = product.name
                    binding.quantityTextView.text = "${product.quantity} шт"

                    val expirationDays = getDaysUntilExpiration(product.expirationDate)
                    if (expirationDays <= 0) {
                        binding.expirationTextView.text = "Просрочено"
                        binding.productContainer.setBackgroundColor(Color.RED)
                    } else {
                        binding.expirationTextView.text = "$expirationDays дней"
                    }

                    if (product.calories > 0) {
                        binding.caloriesTextView.text = "Калории: ${product.calories}"
                    } else {
                        binding.caloriesTextView.visibility = View.GONE
                    }

                    val iconResource = when (product.type) {
                        "fruit" -> R.drawable.fruit_icon
                        "vegetable" -> R.drawable.vegetable_icon
                        else -> R.drawable.default_product_icon
                    }
                    binding.productIcon.setImageResource(iconResource)


                    binding.deleteButton.setOnClickListener { onDelete(product) }
                    binding.checkButton.setOnClickListener { onCheck(product) }
                    binding.minusButton.setOnClickListener { onMinus(product) }
                }
            }
        }

        private fun getDaysUntilExpiration(expirationDate: Long): Int {
            val currentTime = System.currentTimeMillis()
            val diffInMillis = expirationDate - currentTime
            return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (layoutType) {
            PAGE_TYPE_1 -> ItemProductPage1Binding.inflate(inflater, parent, false)
            PAGE_TYPE_2 -> ItemProductPage2Binding.inflate(inflater, parent, false)
            else -> ItemProductBinding.inflate(inflater, parent, false) // Default layout
        }
        return ProductViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product, onDelete, onCheck, onMinus)
    }

    fun updateCalories(calories: Int) {
        totalCalories += calories
    }

    fun removeProduct(product: Product) {
        val currentList = currentList.toMutableList()
        currentList.remove(product)
        submitList(currentList)
    }

    fun addProduct(product: Product) {
        val currentList = currentList.toMutableList()
        currentList.add(product)
        submitList(currentList)
    }

    // Define the DiffUtil callback
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}