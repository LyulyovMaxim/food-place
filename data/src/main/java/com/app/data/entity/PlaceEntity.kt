package com.app.data.entity

import io.realm.RealmObject

data class PlaceEntity(val latitude: Double, val longitude: Double, val name: String?): RealmObject()