package com.example.shortespath

import com.google.android.gms.maps.model.Polyline
import com.google.maps.model.DirectionsLeg

data class PolyLineData(
    val polyline: Polyline,
    val directionsLeg: DirectionsLeg
)