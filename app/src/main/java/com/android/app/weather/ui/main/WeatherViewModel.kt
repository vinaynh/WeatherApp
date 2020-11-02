package com.android.app.weather.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.app.weather.entities.WeatherUser
import com.android.app.weather.repository.UserRepository

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel

    val userRepository = UserRepository(application.applicationContext);

    // Create a LiveData with a String
    val weather: MutableLiveData<WeatherUser> by lazy {
        MutableLiveData<WeatherUser>()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun setGeoCordinates(latitude :String, longitude :String) {
        userRepository.fetchFromDb(latitude,longitude)

    }

}