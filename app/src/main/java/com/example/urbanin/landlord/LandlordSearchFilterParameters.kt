package com.example.urbanin.landlord

data class LandlordSearchFilterParameters(
    var rentMin: Long = 0,
    var rentMax: Long = 0,
    var availableFrom: String = "",
    var minRooms: String = "Any",
    var maxRooms: String = "Any",
    var numBaths: String = "Any",
    var type: String = "",
    var amenities: LinkedHashMap<String, Boolean> = linkedMapOf(),      // specifically using LinkedHashMap to reserve order
    var utilities: LinkedHashMap<String, Boolean> = linkedMapOf(),
    var furnished: String = ""
)
// populate this variable to filter listings
var filterParameters = LandlordSearchFilterParameters()
var ifFiltered: Boolean = false
var filterCount = 0

// for sort
var selectedOptionLandlord = 0
var sortByLandlord: String = "Latest"
var sortOptions = arrayOf("Latest", "Rent: Low to High", "Rent: High to Low", "Number of Rooms", "Number of Baths")