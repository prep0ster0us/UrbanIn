import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.urbanin.R
import com.example.urbanin.databinding.LandlordListingCardBinding
import com.example.urbanin.landlord.LandlordListingCard
import com.example.urbanin.tenant.search.ListingCard


class LandlordListingAdapter(
    private val listings: List<ListingCard>,
    private val listener: ListingItemListener
) : RecyclerView.Adapter<LandlordListingAdapter.ViewHolder>() {

    class ViewHolder(binding: LandlordListingCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.listingItemImage
        val priceTextView: TextView = binding.listingItemPrice
        val descriptionTextView: TextView = binding.listingItemDescription
        val addressTextView: TextView = binding.listingItemAddress
        val editButton: ImageButton = binding.editListingButton
        val deleteButton: ImageButton = binding.deleteListingButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LandlordListingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listing = listings[position]
        holder.apply {
            // Here you should load the image into imageView using an image loading library like Glide or Picasso
            // For example: Glide.with(imageView.context).load(listing.img).into(imageView)
            priceTextView.text = "Listing Price: $${listing.title}"
            descriptionTextView.text = "Listing Description: ${listing.description}"
            addressTextView.text = "Listing Address: ${listing.location}"
//            editButton.setOnClickListener { listener.onEditClicked(listing) }
//            deleteButton.setOnClickListener { listener.onDeleteClicked(listing) }
        }
    }

    override fun getItemCount(): Int {
        return listings.size
    }
}

interface ListingItemListener {
    fun onEditClicked(listing: LandlordListingCard.Listing)
    fun onDeleteClicked(listing: LandlordListingCard.Listing)
}
