package com.android.app.weather.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.android.app.weather.entities.WeatherUser
import com.android.app.weather.repository.UserRepository

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    // Instance of UserRepository to fetch weather data.
    val userRepository = UserRepository(application.applicationContext);

    // MediatorLiveData to observe changes from UserRepository and propagate them to the Fragment.
    // WeatherMainFragment observes this LiveData to update the UI.
    val mediatorLiveData = MediatorLiveData<WeatherUser>()

    // Keep track of the current LiveData source from the repository to avoid adding multiple sources
    // if setGeoCordinates were to be designed to change the source LiveData instance.
    // However, with the current UserRepository.weatherInDB being a single MutableLiveData instance,
    // this is more about ensuring we don't call addSource repeatedly on the same LiveData.
    // For this refinement, we'll add the source in init.
    // private var currentWeatherDataSoure: LiveData<WeatherUser>? = null // Example if source could change

    init {
        // Add userRepository.weatherInDB as a permanent source to mediatorLiveData.
        // Whenever weatherInDB is updated by the repository, mediatorLiveData will also be updated.
        mediatorLiveData.addSource(userRepository.weatherInDB) { weatherUser ->
            mediatorLiveData.value = weatherUser
        }
    }

    override fun onCleared() {
        super.onCleared()
        // It's good practice to remove sources from MediatorLiveData in onCleared,
        // though in this specific case, as the ViewModel is cleared, it might not be strictly necessary
        // if userRepository.weatherInDB is also tied to the application lifecycle or cleared appropriately.
        // However, to be safe and prevent potential leaks if userRepository lived longer:
        // mediatorLiveData.removeSource(userRepository.weatherInDB) // Not strictly needed if repo is app-scoped and LiveData clears observers.
    }

    /**
     * Called by WeatherMainFragment to initiate fetching of weather data
     * for the given latitude and longitude.
     *
     * @param latitude The latitude as a String.
     * @param longitude The longitude as a String.
     */
    fun setGeoCordinates(latitude: String, longitude: String) {
        // Trigger the UserRepository to fetch data from the database or network.
        // The result will be posted to userRepository.weatherInDB,
        // which mediatorLiveData is already observing (due to the init block).
        userRepository.fetchFromDb(latitude, longitude)
    }
}