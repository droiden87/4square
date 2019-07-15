package com.edentech.labs.foursquarevenues.api

import com.edentech.labs.foursquarevenues.mainscreen.ui.models.LocationResult

interface LocationResultsListener {
    fun success(locations: ArrayList<LocationResult>)
    fun failed()
}