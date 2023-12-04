package com.example.urbanin.data

data class SortParameters(
    var selectedOption: Int = 0,
    var sortBy: String = "Latest"
)
val sortOptions = arrayOf("Latest", "Rent: Low to High", "Rent: High to Low", "Number of Rooms", "Number of Baths")
