package com.android.app.weather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.android.app.weather.logger.Logger
import com.android.app.weather.ui.main.WeatherMainFragment
import com.android.app.weather.worker.WeatherUpdateWorker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit


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
            Logger.warn("checkLastKnownLocation: Permission not granted. This should have been caught by checkLocationPermission.")
            Toast.makeText(this, "Location permission not available. Closing app.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->

                    if (location == null) {
                        Logger.warn("fusedLocationClient.lastLocation success, but location is null.")
                        Toast.makeText(this, "Unable to retrieve current location. Closing app.", Toast.LENGTH_LONG).show()
                        finish()
                        return@addOnSuccessListener
                    }
                    latitude = location.latitude;
                    longitude = location.longitude;
                    addWeatherFragment()
                    schedulePeriodicWeatherUpdates() // Schedule background updates

                }.addOnFailureListener { exception ->
                    Logger.error("fusedLocationClient.lastLocation failed: ${exception.message}", exception)
                    Toast.makeText(this, "Failed to get location. Closing app.", Toast.LENGTH_LONG).show()
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
                    Logger.info("onRequestPermissionsResult: Permission granted.")
                    checkLastKnownLocation()
                } else {
                    Logger.warn("onRequestPermissionsResult: Permission denied.")
                    Toast.makeText(this, "Location permission denied. Closing app.", Toast.LENGTH_LONG).show()
                    finish();
                }
                return
            }

        }
    }

    private fun schedulePeriodicWeatherUpdates() {
        Logger.info("Attempting to schedule periodic weather updates.")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // Wi-Fi
            .build()

        val weatherUpdateRequest =
            PeriodicWorkRequestBuilder<WeatherUpdateWorker>(
                repeatInterval = 6, // Repeat every 6 hours
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "WeatherUpdateWorkerPeriodic", // Unique name for the work
            ExistingPeriodicWorkPolicy.KEEP, // Keep the existing work if it's already scheduled
            weatherUpdateRequest
        )
        Logger.info("Periodic weather update request enqueued.")
    }
}