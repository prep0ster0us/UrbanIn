package com.example.urbanin.tenant.search

import java.text.SimpleDateFormat
import java.util.Locale

object FilterListingUtil {
    fun checkFilterBath(listing: String, filter: String): Boolean {
        return if (filter == "Any") {
            false
        } else {
            val bathList = linkedMapOf(
                // TODO: adjust these filters later ('Any' and '1' are the same)
                "1+" to listOf("1", "1.5", "2", "3", "4"),
                "1.5+" to listOf("1.5", "2", "3", "4"),
                "2+" to listOf("2", "3", "4"),
                "3+" to listOf("3", "4"),
                "4+" to listOf("4")
            )
            listing !in bathList[filter]!!
        }
//
    }

    fun checkFilterRooms(numRooms: String, minRooms: String, maxRooms: String): Boolean {
        val roomList = listOf("Studio", "1", "2", "3", "4")
        return if (minRooms == "Any") {
            if (maxRooms == "Any") {
                // no filter, no listings to be removed
                false
            } else {
                // listing should have numRooms > maxRooms, to be removed
                roomList.indexOf(numRooms) > roomList.indexOf(maxRooms)
            }
        } else {
            if (maxRooms == "Any") {
                // listing should have numRooms < minRooms, to be removed
                roomList.indexOf(numRooms) < roomList.indexOf(minRooms)
            } else {
                // listing should NOT have minRooms <= numRooms <= maxRooms, to be removed
                (roomList.indexOf(numRooms) < roomList.indexOf(minRooms)) or
                        (roomList.indexOf(numRooms) > roomList.indexOf(maxRooms))
            }
        }
    }

    fun compareDates(filterDate: String, listingDate: String): Boolean {
        if (filterDate.isBlank()) {
            return false
        }
        val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.US)
        return dateFormat.parse(listingDate) > dateFormat.parse(filterDate)
    }

    fun checkFilterHashMap(
        listing: Map<String, Boolean>,
        filter: LinkedHashMap<String, Boolean>
    ): Boolean {
        // if all false, not filter applicable
        if (!filter.values.contains(true)) {
            return false
        } else {
            // if atleast one of the filter parameter values are set
            // match key set for filter and listing
            for ((key, value) in filter) {
                if (value) {
                    if (listing[key] == false)
                        return true
                }
            }
            return false
        }
    }
}