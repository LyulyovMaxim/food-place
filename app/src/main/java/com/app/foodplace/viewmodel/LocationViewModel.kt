package com.app.foodplace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.app.domain.data.Place
import com.app.domain.repository.ILocationRepository
import com.app.domain.repository.IPlacesRepository
import com.esri.arcgisruntime.geometry.Point

class LocationViewModel(
    private val locationRepository: ILocationRepository,
    private val placesRepository: IPlacesRepository
) : ViewModel() {

    private val _point = MutableLiveData<Point>()

    val nearbyData: LiveData<List<Place>> = _point.switchMap {
        placesRepository.refreshNearbyPlaces(it)
    }

    fun getDeviceLocation() = locationRepository.getDeviceLocation()

    fun refreshPlaces(point: Point) {
        _point.value = point
    }
}