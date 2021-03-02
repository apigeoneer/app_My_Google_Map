package com.gmail.apigeoneer.gmapsgplaces

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private var locationPermissionsGranted = false

    companion object {
        private const val TAG = "MapActivity"
        private const val FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
        private const val COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234
        private const val DEFAULT_ZOOM = 15f
    }

//  override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?)       // Activity doesn't load
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        getLocationPermission()
    }

    fun getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation(): getting the device's current location")

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            if (locationPermissionsGranted) {
                val location = fusedLocationProviderClient.lastLocation

                location.addOnCompleteListener(
                        OnCompleteListener { task ->
                            when {
                                task.isSuccessful -> {
                                    Log.d(TAG, "onComplete: found location!")
                                    val currentLocation = task.result

                                    moveCamera(LatLng(currentLocation.latitude, currentLocation.longitude), DEFAULT_ZOOM)
                                } else -> {
                                    Log.d(TAG, "onComplete: current location is null")
                                    Toast.makeText(this, "unable to get the current location", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
            }
        } catch (e : SecurityException) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.message)
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: ${latLng.latitude}, lng: ${latLng.longitude}")
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    // Initializing map
    private fun initMap() {
        Log.d(TAG, "::: initMap: initializing map :::")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // onMapReady Callback
    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d(TAG, "::: Map is ready!!! :::")
        Toast.makeText(this, "Map is ready!!!", Toast.LENGTH_SHORT).show()
        map = googleMap!!
    }

    /**
     * Check for both our permissions, and request a permission from the user when necessary
     * After Marshmallow you need to as explicitly for some permissions at run-time
     */
    private fun getLocationPermission(): Unit {
        Log.d(TAG, "getLocationPermission: getting location permissions")
        // Making an array of all the req.d permissions
        val permissions = arrayOf(FINE_LOCATION, COARSE_LOCATION)

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionsGranted = true
                initMap()
            } else {
                // Permission hasn't been granted, so request it
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
            }
        } else {
            // Permission hasn't been granted, so request it
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * After the user responds to the system permissions dialog, the system then invokes your app's implementation of onRequestPermissionsResult()
     * by passing in the user response to the permission dialog, as well as the request code that you defined
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "::: onRequestPermissionsResult: called. :::")
        locationPermissionsGranted = false

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults.indices) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionsGranted = false
                            // initialize the map
                            Log.d(TAG, "onRequestPermissionsResult: permission denied")
                            return
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted")
                    locationPermissionsGranted = true
                    // initialize our map
                    initMap()
                }
            }
        }
    }
}