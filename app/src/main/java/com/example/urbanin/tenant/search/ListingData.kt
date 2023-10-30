package com.example.urbanin.tenant.search

import android.widget.ImageView

data class ListingData(
    var listingID: String = "",
    var userID: String = "",
    var type: String = "",
    var title: String = "",
    var description: String = "",
    var location: String = "",
    var price: Long = 0,
    var img: String = "",       // TODO: change to image type
    var datePosted: String = "",
    var availableFrom: String = "",
    var numRooms: Long = 0,
    var numBaths: Long = 0,
    var petsAllowed: String = "",
    var utilities: ArrayList<String> = arrayListOf(),
    var amenities: ArrayList<String> = arrayListOf()
)

var listingCollection: ArrayList<ListingData> =
    arrayListOf(      // TODO: Only for debugging, need to populate using data stored in firestore
        ListingData(
            "listingID",
            "userID",
            "apartment",
            "XYZ apartments",
            "2 bd | 2 ba | 1500 sqft",
            "New Haven, CT",
            1200,
            "",
            "today",
            "tomorrow",
            2,
            2,
            "dogs",
            arrayListOf(),
            arrayListOf()
        ), ListingData(
            "listingID",
            "userID",
            "apartment",
            "XYZ apartments",
            "2 bd | 2 ba | 1500 sqft",
            "New Haven, CT",
            1200,
            "",
            "today",
            "tomorrow",
            2,
            2,
            "dogs",
            arrayListOf(),
            arrayListOf()
        ), ListingData(
            "listingID",
            "userID",
            "apartment",
            "XYZ apartments",
            "2 bd | 2 ba | 1500 sqft",
            "New Haven, CT",
            1200,
            "",
            "today",
            "tomorrow",
            2,
            2,
            "dogs",
            arrayListOf(),
            arrayListOf()
        ), ListingData(
            "listingID",
            "userID",
            "apartment",
            "XYZ apartments",
            "2 bd | 2 ba | 1500 sqft",
            "New Haven, CT",
            1200,
            "",
            "today",
            "tomorrow",
            2,
            2,
            "dogs",
            arrayListOf(),
            arrayListOf()
        )
    )