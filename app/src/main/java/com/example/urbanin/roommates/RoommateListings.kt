package com.example.urbanin.roommates

import java.util.Date

class RoommateListings {

    data class RoommateListing(
        val ownerId: String,
        val location: String,
        val price: Double,
        val images: List<String>,
        val details: RoommateDetails,
        val roommatePreferences: RoommatePreferences,
        val createdAt: Date,
        val isActive: Boolean,
        val viewsCount: Int,
        val interestedUsers: List<String>
    )

    data class RoommateDetails(
        val numberOfRooms: Int,
        val sharedSpaces: List<String>,
        val furnishing: String,
        val availabilityDate: Date
    )

    data class RoommatePreferences(
        val genderPreference: String,
        val ageRange: String,
        val lifestylePreferences: List<String>
    )

}