package com.example.urbanin.data

import java.text.SimpleDateFormat
import java.util.Locale

object FilterListingUtil {
    fun checkIfMatches(listing: ListingData, filter: FilterParameters): Boolean {
        return arrayOf(
            comparePrices(listing.price, filter.rentMin, filter.rentMax),
            compareDates(listing.availableFrom, filter.availableFrom),
            compareRoom(listing.numRooms, filter.minRooms, filter.maxRooms),
            compareBath(listing.numBaths, filter.numBaths),
            compareType(listing.type, filter.type),
            checkFilterHashMap(listing.amenities, filter.amenities),
            checkFilterHashMap(listing.utilities, filter.utilities),
            compareFurnished(listing.furnished, filter.furnished)
        ).contains(false)
    }
    private fun comparePrices(price: String, minPrice: Long, maxPrice: Long): Boolean {
        return price.toLong() in minPrice..maxPrice
    }

    private fun compareDates(listingDate: String, filterDate: String): Boolean {
        if (filterDate.isBlank())
            return true
        val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.US)
        return dateFormat.parse(listingDate) <= dateFormat.parse(filterDate)
    }

    private fun compareRoom(numRooms: String, minRooms: String, maxRooms: String): Boolean {
        val roomList = listOf("Studio", "1", "2", "3", "4")
        return if (minRooms == "Any") {
            if (maxRooms == "Any") {
                // no max/min filter, all listings match filter condition
                true
            } else {
                // no min filter, listing needs to match max filter
                roomList.indexOf(numRooms) <= roomList.indexOf(maxRooms)
            }
        } else {
            if (maxRooms == "Any") {
                // no max filter, listing needs to match min filter
                roomList.indexOf(numRooms) >= roomList.indexOf(minRooms)
            } else {
                // BOTH max/min filter, listings should be within min <= .. <= max range
                with(roomList) {
                    indexOf(numRooms) in indexOf(minRooms)..indexOf(maxRooms)
                }
            }
        }
    }

    private fun compareBath(numBaths: String, filterBaths: String): Boolean {
        return if (filterBaths == "Any") {
            true
        } else {
            val bathList = linkedMapOf(
                // TODO: adjust these filters later ('Any' and '1' are the same)
                "1+" to listOf("1", "1.5", "2", "3", "4"),
                "1.5+" to listOf("1.5", "2", "3", "4"),
                "2+" to listOf("2", "3", "4"),
                "3+" to listOf("3", "4"),
                "4+" to listOf("4")
            )
            numBaths in bathList[filterBaths]!!
        }
    }

    private fun compareType(listing: String, filter: String): Boolean {
        if(filter.isEmpty())
            return true
        return listing == filter
    }

    private fun checkFilterHashMap(
        listing: Map<String, Boolean>,
        filter: LinkedHashMap<String, Boolean>
    ): Boolean {
        if (!filter.values.contains(true)) {
            // if atleast one of the filter parameter values are set
            // match key set for filter and listing
            for ((key, value) in filter) {
                if (value) {
                    if (listing[key] == false)
                        // amenity/utility item checked for filter, but not in listing map; does not match
                        return false
                }
            }
            return true
        } else {
            // if not filter condition set, ALL listings match
            return true
        }
    }

    private fun compareFurnished(listing: String, filter: String): Boolean {
        if(filter.isEmpty())
            return true
        return listing == filter
    }

}