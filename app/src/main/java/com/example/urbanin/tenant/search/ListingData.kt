package com.example.urbanin.tenant.search

import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListingData(
    var listingID: String = "",
    var userID: String = "",
    var type: String = "",
    var title: String = "",
    var description: String = "",
//    var latitude: Double = 0.00,
//    var longitude: Double = 0.00,
    var latitude: String = "",
    var longitude: String = "",
    var address: String = "",
//    var price: Long = 0,
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

var listingCollection: MutableList<ListingData> = mutableListOf()
//    arrayListOf(      // TODO: Only for debugging, need to populate using data stored in firestore
//        ListingData(
//            "listingID",
//            "userID",
//            "apartment",
//            "XYZ apartments",
//            "2 bd | 2 ba | 1500 sqft",
//            GeoPoint(41.29030196271535, -72.96002113889433),
//            "New Haven, CT",
//            1200,
//            "",
//            "today",
//            "tomorrow",
//            2,
//            2,
//            "dogs",
//            hashMapOf(
//                "water" to true,
//                "gas" to true,
//                "electricity" to false
//            ),
//            hashMapOf(
//                "gym" to true,
//                "pool" to false
//            )
//        ), ListingData(
//            "listingID",
//            "userID",
//            "apartment",
//            "XYZ apartments",
//            "2 bd | 2 ba | 1500 sqft",
//            GeoPoint(41.28927135742264, -72.9649934473006),
//            "New Haven, CT",
//            1200,
//            "",
//            "today",
//            "tomorrow",
//            2,
//            2,
//            "dogs",
//            hashMapOf(
//                "water" to true,
//                "gas" to true,
//                "electricity" to false
//            ),
//            hashMapOf(
//                "gym" to true,
//                "pool" to false
//            )
//        ), ListingData(
//            "listingID",
//            "userID",
//            "apartment",
//            "XYZ apartments",
//            "2 bd | 2 ba | 1500 sqft",
//            GeoPoint(41.291082339711515, -72.96768962231839),
//            "New Haven, CT",
//            1200,
//            "",
//            "today",
//            "tomorrow",
//            2,
//            2,
//            "dogs",
//            hashMapOf(
//                "water" to true,
//                "gas" to true,
//                "electricity" to false
//            ),
//            hashMapOf(
//                "gym" to true,
//                "pool" to false
//            )
//        )
//    )
