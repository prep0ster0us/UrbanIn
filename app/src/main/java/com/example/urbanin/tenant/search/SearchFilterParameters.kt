package com.example.urbanin.tenant.search

data class SearchFilterParameters(
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
var filterParameters = SearchFilterParameters()
var ifFiltered: Boolean = false
var filterCount = 0
