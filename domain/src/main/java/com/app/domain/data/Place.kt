package com.app.domain.data

import com.esri.arcgisruntime.geometry.Point

data class Place(
    val name: String,
    val type: String?,
    val location: Point,
    val address: String?,
    val URL: String?,
    val phone: String?
)