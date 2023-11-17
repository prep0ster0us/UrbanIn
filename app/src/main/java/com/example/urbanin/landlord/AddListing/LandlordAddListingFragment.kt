package com.example.urbanin.landlord.AddListing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.urbanin.databinding.LandlordFragmentAddListingBinding


class LandlordAddListingFragment : Fragment() {
    private lateinit var binding: LandlordFragmentAddListingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LandlordFragmentAddListingBinding.inflate(layoutInflater)

        setupAmenitiesGrid()
        setupUtilitiesGrid()
    }

    private fun setupUtilitiesGrid() {
        binding.util1.itemText.text = "Electricity"
        binding.util2.itemText.text = "Gas"
        binding.util3.itemText.text = "Water"
        binding.util4.itemText.text = "Trash Removal"
        binding.util5.itemText.text = "Snow Removal"
    }
    private fun setupAmenitiesGrid() {
        binding.amen1.itemText.text = "Pets Allowed"
        binding.amen2.itemText.text = "In-Unit laundry"
        binding.amen3.itemText.text = "HVAC System"
        binding.amen4.itemText.text = "24/7 Security"
        binding.amen5.itemText.text = "Gym"
        binding.amen6.itemText.text = "Pool"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }


}