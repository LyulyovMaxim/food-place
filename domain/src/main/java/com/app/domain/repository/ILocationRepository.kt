package com.app.domain.repository

import android.location.Location
import androidx.lifecycle.LiveData

interface ILocationRepository {

    fun getDeviceLocation(): LiveData<Location?>
}