package com.example.shortespath


import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shortespath.Constants.MAPVIEW_BUNDLE_KEY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import kotlinx.android.synthetic.main.fragment_maps.view.*


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnPolylineClickListener {

    private lateinit var mView: View
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mMapBoundary: LatLngBounds
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var mGeoApiContext: GeoApiContext
    private lateinit var mPolyLinesData: ArrayList<PolyLineData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_maps, container, false)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)

        mPolyLinesData = ArrayList()

        initMaps(savedInstanceState)

        return mView
    }

    private fun initMaps(savedInstanceState: Bundle?) {

        var mapViewBundle: Bundle? = null
        savedInstanceState?.let {
            mapViewBundle = it.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mView.map_view.onCreate(mapViewBundle)
        mView.map_view.getMapAsync(this)

        mGeoApiContext = GeoApiContext.Builder()
            .apiKey(activity!!.getString(R.string.google_maps_api_key))
            .build()
    }

    private fun calculateDirections(marker: Marker) {
        Log.d(TAG, "calculateDirections: calculating directions.")

        val destination = com.google.maps.model.LatLng(
            marker.position.latitude,
            marker.position.longitude
        )
        val directions = DirectionsApiRequest(mGeoApiContext)

        directions.alternatives(true)
        directions.origin(
            com.google.maps.model.LatLng(
                location.latitude,
                location.longitude
            )
        )
        Log.d(TAG, "calculateDirections: destination: $destination")
        directions.destination(destination).setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString())
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration)
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance)
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString())

                addPolylinesToMap(result)

            }

            override fun onFailure(e: Throwable) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.message)

            }
        })
    }

    private fun setupMarker(googleMap: GoogleMap) {
        googleMap.setOnMapClickListener {
            val markerOptions = MarkerOptions()

            // Setting the position for the marker
            markerOptions.position(it)

            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title("${it.latitude} : ${it.longitude}")

            // Clears the previously touched position
            googleMap.clear()

            // Animating to the touched position
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(it))

            // Placing a marker on the touched position
            googleMap.addMarker(markerOptions)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mView.map_view.onSaveInstanceState(mapViewBundle)
    }

    private fun setCameraView() {

        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            return


        mFusedLocationProviderClient.lastLocation.addOnCompleteListener {
            if (it.isComplete) {
                location = it.result!!

                val mBottomBoundary = location.latitude - 0.01
                val mLeftBoundary = location.longitude - 0.01
                val mTopBoundary = location.latitude + 0.01
                val mRightBoundary = location.longitude + 0.01

                mMapBoundary =
                    LatLngBounds(LatLng(mBottomBoundary, mLeftBoundary), LatLng(mTopBoundary, mRightBoundary))

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0))

            }
        }

    }

    override fun onResume() {
        super.onResume()
        mView.map_view.onResume()
    }

    override fun onStart() {
        super.onStart()
        mView.map_view.onStart()
    }

    override fun onStop() {
        super.onStop()
        mView.map_view.onStop()
    }

    override fun onMapReady(map: GoogleMap) {

        mGoogleMap = map

        mGoogleMap.setOnInfoWindowClickListener(this)

        mGoogleMap.setOnPolylineClickListener(this)

        map.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Marker"))

        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            return

        map.isMyLocationEnabled = true

        setCameraView()

        setupMarker(mGoogleMap)
    }

    override fun onPause() {
        mView.map_view.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mView.map_view.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mView.map_view.onLowMemory()
    }

    companion object {
        val TAG = MainActivity::class.java.canonicalName
    }

    private fun addPolylinesToMap(result: DirectionsResult) {

        Handler(Looper.getMainLooper()).post(object : Runnable {

            override fun run() {

                if (mPolyLinesData.size > 0) {
                    for (polyLineData in mPolyLinesData)
                        polyLineData.polyline.remove()

                    mPolyLinesData.clear()
                    mPolyLinesData = ArrayList()
                }

                Log.d(TAG, "run: result routes: " + result.routes.size)
                for (route in result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString())
                    val decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath())
                    val newDecodedPath = ArrayList<LatLng>()
                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (latLng in decodedPath) {
                        // Log.d(TAG, "run: latlng: " + latLng.toString());
                        newDecodedPath.add(
                            LatLng(
                                latLng.lat,
                                latLng.lng
                            )
                        )
                    }
                    val polyline = mGoogleMap.addPolyline(PolylineOptions().addAll(newDecodedPath))
                    polyline.color = ContextCompat.getColor(activity!!, R.color.darkGrey)
                    polyline.isClickable = true

                    mPolyLinesData.add(PolyLineData(polyline, route.legs[0]))
                }
            }
        })
    }

    override fun onInfoWindowClick(marker: Marker) {
        if (marker.snippet == "This is you") {
            marker.hideInfoWindow()
        } else {

            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Get Direction?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    calculateDirections(marker)
                }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onPolylineClick(p0: Polyline?) {

        for (polyLineData in mPolyLinesData) {

            Log.d(TAG, "onPolylineClick: toString: $polyLineData")

            if (p0!!.id == polyLineData.polyline.id) {
                polyLineData.polyline.color = ContextCompat.getColor(activity!!, R.color.blue1);
                polyLineData.polyline.zIndex = 1f
            } else {
                polyLineData.polyline.color = ContextCompat.getColor(activity!!, R.color.darkGrey);
                polyLineData.polyline.zIndex = 0f
            }
        }
    }

}
