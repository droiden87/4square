package com.edentech.labs.foursquarevenues.api

import com.edentech.labs.foursquarevenues.details.models.DetailsResponse
import com.edentech.labs.foursquarevenues.mainscreen.models.FoursquareResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Service endpoints for interacting with the Foursquare API
 */
interface FoursquareService {
    companion object {
        private const val CLIENT_ID = "PPF0BUPBUQCLYWTUNA5KTZH2WC1QYXFIRIZNUIRVSS40GWRG"
        private const val CLIENT_SECRET = "5NTBA0SOSRV0CV24DBQ25WOL43KXKK35NEAS1NRXQ4IEAKYY"
        private const val VERSION = "20190712"
        private const val COMMON_PARAMS = "&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&v=$VERSION"

        //47.606200,-122.332100     ll=47.606200,-122.332100&
    }

    @GET("/v2/venues/search?limit=20$COMMON_PARAMS")
    fun getLocationResults(@Query("query") query: String, @Query("ll") latlng: String): Call<FoursquareResponse>

    @GET("/v2/venues/{venue_id}/?$COMMON_PARAMS")
    fun getDetails(@Path("venue_id") venueId: String): Call<DetailsResponse>
}