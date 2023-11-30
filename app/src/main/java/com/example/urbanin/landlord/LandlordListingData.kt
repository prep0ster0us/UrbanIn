package com.example.urbanin.landlord

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LandlordListingData(
    var listingID: String = "",
    var userID: String = "",
    var type: String = "",
    var title: String = "",
    var description: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var address: String = "",
    var price: String = "",
    var img: MutableList<String> = mutableListOf(),
    var vid: MutableList<String> = mutableListOf(),
    var datePosted: String = "",
    var availableFrom: String = "",
    var numRooms: String = "",
    var numBaths: String = "",
    var furnished: String = "",
    var utilities: Map<String, Boolean> = hashMapOf(),
    var amenities: Map<String, Boolean> = hashMapOf()
) : Parcelable

var userListingCollection: MutableList<LandlordListingData> = arrayListOf()
