package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemProductBinding
import com.example.myapplication.database.Product

class ProductAdapter(private val productList: MutableList<Product>, private val onDelete: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, onDelete: (Product) -> Unit) {
            binding.productNameTextView.text = product.name
            binding.quantityTextView.text = "${product.quantity} шт"
            binding.deleteButton.setOnClickListener { onDelete(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product, onDelete)
    }

    override fun getItemCount(): Int = productList.size

    fun addProduct(product: Product) {
        productList.add(product)
        notifyItemInserted(productList.size - 1)
    }

    fun removeProduct(product: Product) {
        val index = productList.indexOf(product)
        if (index >= 0) {
            productList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}