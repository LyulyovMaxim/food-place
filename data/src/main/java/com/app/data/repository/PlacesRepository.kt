package com.app.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.domain.data.Place
import com.app.domain.repository.IPlacesRepository
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult
import com.esri.arcgisruntime.tasks.geocode.LocatorTask

class PlacesRepository: IPlacesRepository {
    private val TAG = "{" + PlacesRepository::class.java.simpleName + "}"

    private val GEOCODE_URL = "https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer"
    private val RESULT_COUNT = 20
    private val sLocatorTask: LocatorTask by lazy { LocatorTask(GEOCODE_URL) }

    private val places = MutableLiveData<List<Place>>()

    override fun refreshNearbyPlaces(point: Point): LiveData<List<Place>> {
        findPlaces(point)
        return places
    }

    private fun findPlaces(point: Point) {
        val parameters = GeocodeParameters().apply {
            maxResults = RESULT_COUNT
            preferredSearchLocation = point
            resultAttributeNames.clear()
            resultAttributeNames.add("*")
            categories.apply {
                add("Food")
                add("Pizza")
                add("Coffee Shop")
                add("Bar or Pub")
            }
        }

        val results: ListenableFuture<List<GeocodeResult>> =
            sLocatorTask.geocodeAsync("", parameters)
        Log.i(TAG, "Geocode search started...")

        results.addDoneListener {
            Log.d(TAG, "results done loading: results=[$results]")
            places.postValue(results.get()
                .map {
                    val attributes: Map<String, Any> = it.attributes
                    val address = attributes["Place_addr"] as String?
                    val name = attributes["PlaceName"] as String?
                    val phone = attributes["Phone"] as String?
                    val url = attributes["URL"] as String?
                    val type = attributes["Type"] as String?
                    val location: Point = it.displayLocation

                    Place(name.orEmpty(), type, location, address, url, phone)
                })
        }
    }
}