package com.edentech.labs.foursquarevenues.mainscreen.ui.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edentech.labs.foursquarevenues.api.FoursquareServiceProvider
import com.edentech.labs.foursquarevenues.mainscreen.models.FoursquareResponse
import com.edentech.labs.foursquarevenues.mainscreen.ui.models.LocationResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainScreenViewModel : ViewModel() {
    companion object {
        const val TAG = "MainScreenViewModel"
        const val ERROR_CODE_RETRIEVE = 500
        const val ERROR_CODE_NO_CURRENT_LOCATION = 501
    }
    var searchFilter: String = ""
    var locationResults: MutableLiveData<ArrayList<LocationResult>> = MutableLiveData()
    var locationResultsError: MutableLiveData<Int> = MutableLiveData()


    private fun getLocationResults(query: String, currentLocation: Location) {
        if (query.isBlank()) {
            Log.d(TAG, "Query empty so no request will be made")
            locationResults.postValue(ArrayList())
            return
        }
        val currentLatLng = currentLocation.latitude.toString() + "," + currentLocation.longitude.toString()
        FoursquareServiceProvider.service.getLocationResults(query = query, latlng = currentLatLng).enqueue(object : Callback<FoursquareResponse> {
            override fun onFailure(call: Call<FoursquareResponse>, t: Throwable) {
                Log.e(javaClass.simpleName, t.message)
                locationResultsError.postValue(ERROR_CODE_RETRIEVE)
            }

            override fun onResponse(call: Call<FoursquareResponse>, response: Response<FoursquareResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Success: " + response.raw().request().url().toString())
                    //successful response so parse the results and post them to the awaiting live data
                    response.body()?.let { foursquareResponse ->
                        locationResults.postValue(buildResults(foursquareResponse))
                    }
                } else {
                    onFailure(call, Throwable("Unsuccessful request for locations: " + response.code()))
                }
            }
        })
    }

    fun setQuery(query: String, currentLocation: Location?) {
        searchFilter = query

        refresh(currentLocation)
    }

    fun refresh(currentLocation: Location?) {
        if (currentLocation == null) {
            locationResultsError.postValue(ERROR_CODE_NO_CURRENT_LOCATION)
            return
        }

        getLocationResults(searchFilter, currentLocation)
    }

    /**
     * Build the list of location results sorted by distance
     *
     * @return empty list of no results were found in the response
     */
    private fun buildResults(foursquareResponse: FoursquareResponse): ArrayList<LocationResult> {
        foursquareResponse.response?.let { response ->
            val resultsList = ArrayList<LocationResult>()
            response.venues?.forEach { resultsList.add(LocationResult(it)) }

            return ArrayList(resultsList.sortedBy { it.locationDistance })
        }

        return ArrayList()
    }
}