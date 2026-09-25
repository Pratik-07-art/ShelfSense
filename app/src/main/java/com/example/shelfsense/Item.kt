package com.example.shelfsense

data class Item(
    val id: String,
    var name: String,
    var category: String,
    var quantity: Int,
    var unit: String,
    var purchaseDate: String,
    var expiryDate: String,
    var notes: String
)