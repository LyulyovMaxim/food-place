package com.app.domain.repository

import androidx.lifecycle.LiveData
import com.app.domain.data.Place
import com.esri.arcgisruntime.geometry.Point

interface IPlacesRepository {

    fun refreshNearbyPlaces(point: Point): LiveData<List<Place>>
}