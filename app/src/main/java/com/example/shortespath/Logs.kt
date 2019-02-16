package com.example.shortespath

import com.google.maps.model.Distance
import com.google.maps.model.Duration
import com.google.maps.model.LatLng

data class Logs(
    val origin: LatLng,
    val destination: LatLng,
    val waypoints: ArrayList<com.google.maps.model.LatLng>,
    val duration: Duration,
    val distance: Distance
)