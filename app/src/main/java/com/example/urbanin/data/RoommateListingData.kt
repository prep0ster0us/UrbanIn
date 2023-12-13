package com.example.urbanin.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RoommateListingData (
    var listingID: String = "",
    var userID: String = "",
    var age: Long = 0,
    var name: String = "",
    var gender: String = "",
    var occupation: String = "",
    var budget: String = "",
    var description: String = "",
    var roomSize: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var address: String = "",
    var img: String = "",
    var datePosted: String = "",
    var moveInDate: String = "",
    var hobbies: ArrayList<String> = arrayListOf()
) : Parcelable