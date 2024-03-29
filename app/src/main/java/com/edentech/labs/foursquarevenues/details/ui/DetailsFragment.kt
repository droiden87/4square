package com.edentech.labs.foursquarevenues.details.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.edentech.labs.foursquarevenues.R
import com.edentech.labs.foursquarevenues.databinding.FragmentDetailsBinding
import com.edentech.labs.foursquarevenues.details.models.VenueDetail
import com.edentech.labs.foursquarevenues.details.viewmodels.DetailsViewModel
import com.edentech.labs.foursquarevenues.helpers.Errors
import com.edentech.labs.foursquarevenues.helpers.MapUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_details.*

/**
 * Fragment to display location details
 */
class DetailsFragment : Fragment() {
    companion object {
        private const val PERMISSION_CALL_REQUEST_CODE = 124
    }
    private val viewModel: DetailsViewModel by lazy { ViewModelProviders.of(this).get(DetailsViewModel::class.java) }
    private var snackBar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate view and obtain an instance of the binding class.
        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)

        // Specify the current fragment as the lifecycle owner
        binding.lifecycleOwner = this

        //assign the view model to be bound
        binding.model = viewModel
        binding.handlers = DetailsClickHandlers()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //handle bad data by going back if no location id
        val locationId = viewModel.locationId
        if (locationId.isBlank()) {
            activity?.onBackPressed()
            return
        }

        dismissSnackBar()
        viewModel.details.observe(viewLifecycleOwner, Observer { venueDetail ->
            if (venueDetail == null) {
                //no data returned which is indicative of an error case, so show an error message
                activity?.let {
                    activity?.findViewById<CoordinatorLayout>(R.id.details_coordinator)?.let {
                        snackBar = Errors.showError(it, R.string.request_failed_details, R.string.retry, View.OnClickListener {
                            dismissSnackBar()
                            viewModel.refresh()
                        })
                    }
                }
            } else {
                //data returned successfully so lets populate the screen
                populateScreen(venueDetail)
            }
        })
        viewModel.refresh()
    }

    /**
     * Dismiss any error message that is showing
     */
    private fun dismissSnackBar() {
        snackBar?.let {
            it.dismiss()
            snackBar = null
        }
    }

    /**
     * Populates the screen with the data retrieved from the API
     */
    private fun populateScreen(venueDetail: VenueDetail?) {
        if (venueDetail == null || !isAdded || context == null) {
            activity?.onBackPressed()
            return
        }

        //set the ratings bar to the color provided if its available
        context?.let {
            DrawableCompat.setTint(details_rating_bar.progressDrawable, ContextCompat.getColor(it, R.color.disabled_grey))
        }
        venueDetail.ratingColor?.let { ratingColor ->
            if (ratingColor != "null") {
                DrawableCompat.setTint(details_rating_bar.progressDrawable, Color.parseColor("#$ratingColor"))
            }
        }

        //phone button
        val number = venueDetail.contact?.phone
        details_phone.setOnClickListener {
            handleCall(number?:"")
        }

        setupMap(venueDetail)
    }


    /**
     * Setup the map with current location and venue location pinned
     */
    private fun setupMap(venueDetail: VenueDetail) {
        (fragmentManager?.findFragmentById(R.id.details_map) as SupportMapFragment).getMapAsync { googleMap ->
            val latlngBuilder = LatLngBounds.Builder()

            googleMap.uiSettings.isScrollGesturesEnabled = false
            googleMap.uiSettings.isZoomGesturesEnabled = false
            googleMap.setOnMarkerClickListener { true } //disable click on pins

            //pin the pivot location
            viewModel.currentLocation?.let {latlng ->
                MapUtils.setPivotMarker(getString(R.string.current_location_label), googleMap, latlng.latitude, latlng.longitude)
                latlngBuilder.include(latlng)
            }

            //if all fields exist pin the detail location
            venueDetail.location?.lat?.let { lat ->
                venueDetail.location?.lng?.let { lng ->
                    latlngBuilder.include(LatLng(lat, lng))
                    MapUtils.setMarker(googleMap, lat, lng, venueDetail.name)
                }
            }

            //move camera to the detail location without animation, add padding to sides of map area
            val padding = (resources.configuration.smallestScreenWidthDp * 0.40).toInt()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), padding))
        }
    }



    /*
    private fun setVenueImg(venueDetail: VenueDetail){

        if (VenueDetail.photo0_height > 0) {

            val photoSize: String = VenueDetail.photo0_width.toString() + "x" +
                    VenueDetail.photo0_height.toString()

            val photoPath = VenueDetail.photo0_prefix + photoSize +
                    VenueDetail.photo0_suffix


            Picasso.get().load(photoPath).fit().centerCrop()
                .placeholder(R.drawable.progress_animation)
                .into(venueImageView)
           }
    }
    */


    private fun handleCall(number: String) {
        if (ContextCompat.checkSelfPermission(activity as Context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity as Activity, arrayOf( Manifest.permission.CALL_PHONE), PERMISSION_CALL_REQUEST_CODE)
            return
        }

        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CALL_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //permission denied by user
                    details_phone.isEnabled = false
                } else {
                    //permission granted
                    val number = viewModel.details.value?.contact?.phone
                    if (!number.isNullOrBlank()) {
                        handleCall(number)
                    }
                }
            }
        }
    }
}
