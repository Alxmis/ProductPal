package com.example.myapplication.database

data class Product(
    val name: String = "Unknown",
    var quantity: Int = 1,
    val price: Double = 0.0,
    val expirationDate: Long = 0L,
    val calories: Int = 0,
    val icon: Int = 0,
    var isChecked: Boolean = false,
    val type: String = "Unknown"
)