package com.android.app.weather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.app.weather.logger.Logger
import com.android.app.weather.ui.main.WeatherMainFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class WeatherActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val REQUEST_CODE = 100

    private var latitude: Double = 0.0;
    private var longitude: Double = 0.0;

    private var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //Check for location permission
        //If user denies location permission finish the activity
        //If user grants location permission check the last location from fusedLocationprovider
        //From the last location, extract longitude and latitude .If longitude or latitude is null or if there is failure from fused location provider, finish the activity
        //Pass longitude and latitude to Fragment
        //Fragment will create view model scoped to WeatherActivity lifeCycle
        //Fragment will pass latitude and longitude to view model.view model will expose weather info as live date to fragment
        //View model will query user repository using latitude and longitude.
        //User respository will query DB .If weather info is null , user respository will fetch from network using retrofit library
        //Use workmanager to set periodic work request with wifi constraint to fetch weather information from background
        //Update logic to query network request only once in 2 hrs

        this.savedInstanceState = savedInstanceState
        checkLocationPermission()

    }

    fun addWeatherFragment() {
        runOnUiThread {

            if (savedInstanceState == null) {
                val mainFragment = WeatherMainFragment.newInstance();
                val bundle = Bundle();
                bundle.putString("latitude", latitude.toString())
                bundle.putString("longitude", longitude.toString())
                mainFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, mainFragment)
                        .commitNow()
            }
        }
    }

    fun checkLastKnownLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Logger.info("No permission to get last known location")
            finish()
            return
        }
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->

                    if (location == null) {
                        Logger.info("location is null")
                        finish()
                        return@addOnSuccessListener
                    }
                    latitude = location.latitude;
                    longitude = location.longitude;
                    addWeatherFragment()

                }.addOnFailureListener {
                    Logger.error("Failed to get last location")
                    finish()
                }

    }

    fun checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_CODE)
            return
        } else {
            checkLastKnownLocation()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue
                    Logger.info("permission granted")
                    checkLastKnownLocation()
                } else {
                    Logger.info("permission is denied")
                    finish();
                }
                return
            }

        }
    }
}