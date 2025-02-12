package com.example.myapplication.database

data class Product(
    val name: String = "Unknown",
    val quantity: Int = 1,
    val price: Double = 0.0,
    val expirationDate: Long = 0L,
    val calories: Int = 0
)