package com.edentech.labs.foursquarevenues.mainscreen.ui.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edentech.labs.foursquarevenues.R
import com.edentech.labs.foursquarevenues.glide.GlideApp
import com.edentech.labs.foursquarevenues.mainscreen.ui.models.LocationResult
import kotlinx.android.synthetic.main.location_result_item.view.*
import kotlin.math.roundToInt

/**
 * View holder for display of the location information on the main screen of the app in a list.
 * Allows navigation to the detail screen
 */
class LocationResultViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.location_result_item, parent, false)) {
    fun bind(locationResult: LocationResult, onClickListener: LocationClickListener) {
        val context = itemView.context

        //name and category display
        itemView.locationName.text = locationResult.locationName?: ""
        itemView.locationCategory.text = locationResult.locationCategory?: ""

        //distance display
        itemView.locationDistance.text = ""
        locationResult.locationDistance?.let {meters ->
            //if longer than a mile display miles
            if (meters >= 1609.34) {
                val miles = (meters / 1609.34) //convert to miles
                itemView.locationDistance.text = context.resources.getQuantityString(R.plurals.miles, miles.roundToInt(), String.format("%.1f", miles))
            } else {
                val feet = (meters / 3.28084).roundToInt() //convert to feet
                itemView.locationDistance.text = context.resources.getQuantityString(R.plurals.feet, feet, feet.toString())
            }
        }

        //load the image async with Glide so that the UI doesnt have to wait around on images to load (GlideConfig.kt)
        GlideApp.with(context).load(locationResult.locationIcon).into(itemView.locationImage)

        //send the click event to the listener
        itemView.setOnClickListener{
            locationResult.id?.let {
                onClickListener.onLocationClicked(it)
            }
        }
    }


}