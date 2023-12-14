package com.example.urbanin.tenant.search

import android.app.DatePickerDialog
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.urbanin.MainActivity.Companion.TAG
import com.example.urbanin.R
import com.example.urbanin.data.SearchListingUtil
import com.example.urbanin.data.filterCount
import com.example.urbanin.data.filterParameters
import com.example.urbanin.data.ifFiltered
import com.example.urbanin.databinding.FragmentSearchFilterBinding
import com.example.urbanin.tenant.search.DetailedListing.SearchDetailedListingFragmentArgs
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale

// as independent fragment

class SearchFilterFragment : Fragment() {
//    private var _binding: FragmentSearchFilterBinding? = null
//    private val binding get() = _binding!!

    private lateinit var binding: FragmentSearchFilterBinding

    private lateinit var bedList: Array<String>
    private lateinit var bathList: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentSearchFilterBinding.inflate(layoutInflater)

        setupBedList()
        setupBathList()
        setupTypeGrid()

        with(binding.btnFilterReset) {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                resetFilter()
            }
        }

        with(binding.filterRentRangeSlider) {
            setLabelFormatter { value: Float ->
                val format = NumberFormat.getCurrencyInstance()
                format.maximumFractionDigits = 0
                format.currency = Currency.getInstance("USD")
                format.format(value.toDouble())
            }
            addOnChangeListener { slider, _, _ ->
                binding.filterRentMinHeader.text = formatAsCurrency(slider.values[0])
                binding.filterRentMaxHeader.text = formatAsCurrency(slider.values[1])
            }
        }

        binding.availableDisplayText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnFilterResults.setOnClickListener {
            // set flag to indicate filter to be applied
            ifFiltered = true
            saveFilterParameters()
            setParameterCount()
            // navigate back to search page (where listings will be filtered based on above stored parameters)
            findNavController().navigate(SearchFilterFragmentDirections.navigateTenantFilterToSearch())
        }

        // load in any existing filters
        if (ifFiltered) {
            loadFilterParameters()
        }
    }

    private fun loadFilterParameters() {
        with(binding) {
            with(filterParameters) {
                filterRentRangeSlider.setValues(rentMin.toFloat(), rentMax.toFloat())
                availableDisplayText.text = availableFrom
                if (minRooms != "Any")
                    filterBedroomMin.setSelection(bedList.indexOf(minRooms))

                if (maxRooms != "Any")
                    filterBedroomMax.setSelection(bedList.indexOf(maxRooms))

                if (numBaths != "Any")
                    filterBathroom.setSelection(bathList.indexOf(numBaths))

                if (type.isNotEmpty())
                    setFilterType(type)

                setAmenities(amenities.values.toList())
                setUtilities(utilities.values.toList())
                if (furnished.isNotEmpty())
                    setFilterFurnished(furnished)

            }
        }
    }

    private fun saveFilterParameters() {
        with(filterParameters) {
            rentMin = binding.filterRentMinHeader.text.toString().filter { it.isDigit() }.toLong()
            rentMax = binding.filterRentMaxHeader.text.toString().filter { it.isDigit() }.toLong()
            type = getPropertyType()
            availableFrom = binding.availableDisplayText.text.toString()
            minRooms = binding.filterBedroomMin.selectedItem.toString()
            maxRooms = binding.filterBedroomMax.selectedItem.toString()
            numBaths = binding.filterBathroom.selectedItem.toString()
            amenities = getAmenitiesMap()
            utilities = getUtilitiesMap()
            furnished = getFurnished()
        }
    }

    private fun setFilterFurnished(furnished: String) {
        for (i in 0 until binding.furnishedList.childCount) {
            val childButton = binding.furnishedList.getChildAt(i) as Button
            if (childButton.text.toString() == furnished) {
                binding.furnishedList.check(childButton.id)
            }
        }
    }

    private fun setFilterType(type: String) {
        // check if upper grid contains value
        var isFound = false
        for (i in 0 until binding.typeListTop.childCount) {
            val childButton = binding.typeListTop.getChildAt(i) as Button
            if (childButton.text.toString() == type) {
                binding.typeListTop.check(childButton.id)
                isFound = true
            }
        }
        // if not found in the upper grid, check in bottom grid
        if (!isFound) {
            for (i in 0 until binding.typeListBottom.childCount) {
                val childButton = binding.typeListBottom.getChildAt(i) as Button
                if (childButton.text.toString() == type) {
                    binding.typeListBottom.check(childButton.id)
                }
            }
        }

    }

    private fun setupBedList() {
        bedList = arrayOf("Any", "Studio", "1", "2", "3", "4", "5")
        val adapter = ArrayAdapter<String>(
            requireContext(), R.layout.spinner_text_item_layout, bedList
        )
        binding.filterBedroomMin.adapter = adapter
        binding.filterBedroomMin.setSelection(0)
        binding.filterBedroomMax.adapter = adapter
        binding.filterBedroomMax.setSelection(0)

    }

    private fun setupBathList() {
        bathList = arrayOf("Any", "1+", "1.5+", "2+", "3+", "4+")
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_text_item_layout,
            bathList
        )
        binding.filterBathroom.adapter = adapter
        binding.filterBathroom.setSelection(0)
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
            if (isChecked) {
                when (checkedId) {
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
            if (isChecked) {
                when (checkedId) {
                    binding.type4.id -> bottomList.check(binding.type4.id)
                    binding.type5.id -> bottomList.check(binding.type5.id)
                    binding.type6.id -> bottomList.check(binding.type6.id)
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            requireContext(),
            { _, selectedYear, monthOfYear, dayOfMonth ->
                // Set the selected date using the values received from the DatePicker dialog
                val inputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.US)
                val selectedDate: Date? =
                    inputFormat.parse("${monthOfYear + 1}-$dayOfMonth-$selectedYear")
                // Format the selected date into a string
                val dateFormat =
                    SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.US)
                val formattedDate = selectedDate?.let { dateFormat.format(it) }
                // save selected date from picker in the display text field
                binding.availableDisplayText.text = formattedDate
            },
            // default selected value in date picker dialog = current date
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun getPropertyType(): String {
        return if (binding.typeListTop.checkedButtonId != View.NO_ID) {
            binding.root.findViewById<Button>(binding.typeListTop.checkedButtonId).text.toString()
        } else if (binding.typeListBottom.checkedButtonId != View.NO_ID) {
            binding.root.findViewById<Button>(binding.typeListBottom.checkedButtonId).text.toString()
        } else {
            ""
        }
    }

    private fun getUtilitiesMap(): LinkedHashMap<String, Boolean> {
        return linkedMapOf(
            binding.util1.text.toString() to binding.util1.isChecked,
            binding.util2.text.toString() to binding.util2.isChecked,
            binding.util3.text.toString() to binding.util3.isChecked,
            binding.util4.text.toString() to binding.util4.isChecked,
            binding.util5.text.toString() to binding.util5.isChecked,
        )
    }

    private fun getAmenitiesMap(): LinkedHashMap<String, Boolean> {
        return linkedMapOf(
            binding.amen1.text.toString() to binding.amen1.isChecked,
            binding.amen2.text.toString() to binding.amen2.isChecked,
            binding.amen3.text.toString() to binding.amen3.isChecked,
            binding.amen4.text.toString() to binding.amen4.isChecked,
            binding.amen5.text.toString() to binding.amen5.isChecked,
            binding.amen6.text.toString() to binding.amen6.isChecked
        )
    }

    private fun getFurnished(): String {
        return if (binding.furnishedList.checkedButtonId == View.NO_ID) {
            ""
        } else {
            binding.root.findViewById<Button>(binding.furnishedList.checkedButtonId).text.toString()
        }
    }

    private fun setParameterCount() {
        filterCount = 0
        with(binding) {
            if ((binding.filterRentMinHeader.text.toString() != "$ 0") or (binding.filterRentMaxHeader.text.toString() != "$ 8,000")) {
                Log.d(TAG, "price filter")
                filterCount++
            }
            if (availableDisplayText.text.toString().contains("/")) {
                Log.d(TAG, "date filter")
                filterCount++
            }
            if ((filterBedroomMin.selectedItem.toString() != "Any") or (filterBedroomMax.selectedItem.toString() != "Any")) {
                Log.d(TAG, "bed filter")
                filterCount++
            }
            if (filterBathroom.selectedItem.toString() != "Any") {
                Log.d(TAG, "bath filter")
                filterCount++
            }
            if ((typeListTop.checkedButtonId != View.NO_ID) or (typeListBottom.checkedButtonId != View.NO_ID)) {
                Log.d(TAG, "type filter")
                filterCount++
            }
            if (getAmenitiesMap().values.contains(true)) {
                Log.d(TAG, "amenity filter")
                filterCount++
            }
            if (getUtilitiesMap().values.contains(true)) {
                Log.d(TAG, "utility filter")
                filterCount++
            }
            if (getFurnished().isNotEmpty()) {
                Log.d(TAG, "furnished filter")
                filterCount++
            }
        }
    }

    private fun resetFilter() {
        ifFiltered = false
        // clear layout views
        with(binding) {
            filterRentRangeSlider.setValues(0F, 8000F)
            availableDisplayText.text = ""
            filterBedroomMin.setSelection(0)
            filterBedroomMax.setSelection(0)
            filterBathroom.setSelection(0)
            typeListTop.clearChecked()
            typeListBottom.clearChecked()
            setAmenities(List(6) { false })
            setUtilities(List(5) { false })
            furnishedList.clearChecked()
        }
        // clear filter parameters
        with(filterParameters) {
            rentMin = 0
            rentMax = 0
            availableFrom = ""
            minRooms = ""
            maxRooms = ""
            numBaths = ""
            type = ""
            amenities = linkedMapOf()
            utilities = linkedMapOf()
            furnished = ""
        }
    }

    private fun setAmenities(status: List<Boolean>) {
        binding.amen1.isChecked = status[0]
        binding.amen2.isChecked = status[1]
        binding.amen3.isChecked = status[2]
        binding.amen4.isChecked = status[3]
        binding.amen5.isChecked = status[4]
        binding.amen6.isChecked = status[5]
    }

    private fun setUtilities(status: List<Boolean>) {
        binding.util1.isChecked = status[0]
        binding.util2.isChecked = status[1]
        binding.util3.isChecked = status[2]
        binding.util4.isChecked = status[3]
        binding.util5.isChecked = status[4]
    }

    private fun formatAsCurrency(value: Float): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return "$ " + formatter.format(value).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFilterBack.setOnClickListener {
//            findNavController().navigate(SearchFilterFragmentDirections.navigateTenantFilterToSearch())
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        with(SearchListingUtil) {
            // hide main bottom nav bar
            setTenantNavBarVisibility(requireActivity(), false)
        }
    }
}
