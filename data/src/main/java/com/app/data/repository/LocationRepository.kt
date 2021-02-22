package com.app.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.domain.repository.ILocationRepository
import com.google.android.gms.location.LocationServices

class LocationRepository(private val context: Context) : ILocationRepository {

    private val TAG = "{" + LocationRepository::class.java.simpleName + "}"

    override fun getDeviceLocation(): LiveData<Location?> {
        val result = MutableLiveData<Location?>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val getLocationTask =
                LocationServices.getFusedLocationProviderClient(context).lastLocation
            getLocationTask.addOnCompleteListener {
                Log.d(TAG, "My location: ${getLocationTask.result as Location}")
                result.postValue(getLocationTask.result)
            }
        } else {
            Log.d(TAG, "No permissions for current location")
            result.postValue(null)
        }
        return result
    }
}