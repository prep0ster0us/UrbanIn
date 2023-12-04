package com.example.urbanin.data

data class ListingCard(
    val imgResource: MutableList<String>,
    val title: String,
    val description: String,
    val location: String
)