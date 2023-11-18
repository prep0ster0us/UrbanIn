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
        setupFurnishedGrid()
        setupTypeGrid()
    }

    private fun setupTypeGrid() {
        val topList = binding.typeListTop
        val bottomList = binding.typeListBottom

        binding.typeListTop.addOnButtonCheckedListener { _, checkedId, isChecked ->
            // uncheck selections from bottom grid
            bottomList.uncheck(binding.type4.id)
            bottomList.uncheck(binding.type5.id)
            bottomList.uncheck(binding.type6.id)

            // handle selections from top grid
            if(isChecked) {
                when(checkedId) {
                    binding.type1.id -> topList.check(binding.type1.id)
                    binding.type2.id -> topList.check(binding.type2.id)
                    binding.type3.id -> topList.check(binding.type3.id)
                }
            }
        }

        binding.typeListBottom.addOnButtonCheckedListener { _, checkedId, isChecked ->
            // uncheck selections from top grid
            topList.uncheck(binding.type1.id)
            topList.uncheck(binding.type2.id)
            topList.uncheck(binding.type3.id)

            // handle selections from bottom grid
            if(isChecked) {
                when(checkedId) {
                    binding.type4.id -> bottomList.check(binding.type4.id)
                    binding.type5.id -> bottomList.check(binding.type5.id)
                    binding.type6.id -> bottomList.check(binding.type6.id)
                }
            }
        }
    }

    private fun setupFurnishedGrid() {

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