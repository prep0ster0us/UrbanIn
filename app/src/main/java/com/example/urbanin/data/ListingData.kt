package com.example.urbanin.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListingData(
    var listingID: String = "",
    var userID: String = "",
    var type: String = "",
    var title: String = "",
    var description: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var address: String = "",
    var price: String = "",
    var img: MutableList<String> = mutableListOf(),       // TODO: change to image type
    var vid: MutableList<String> = mutableListOf(),       // TODO: change to video type
    var datePosted: String = "",
    var availableFrom: String = "",
    var numRooms: String = "",
    var numBaths: String = "",
    var furnished: String = "",
    var utilities: Map<String, Boolean> = hashMapOf(),
    var amenities: Map<String, Boolean> = hashMapOf()
) : Parcelable

// TENANT
var listingCollection: MutableList<ListingData> = arrayListOf()
var savedCollection: MutableList<ListingData> = arrayListOf()

// LANDLORD
var userListingCollection: MutableList<ListingData> = arrayListOf()
