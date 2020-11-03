package com.android.app.weather.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.android.app.weather.entities.WeatherUser
import com.android.app.weather.repository.UserRepository

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    val userRepository = UserRepository(application.applicationContext);
    val mediatorLiveData = MediatorLiveData<WeatherUser>()

    override fun onCleared() {
        super.onCleared()
    }

    fun setGeoCordinates(latitude: String, longitude: String) {
        mediatorLiveData.addSource(userRepository.weatherInDB, Observer {
            mediatorLiveData.postValue(it)
        })
        userRepository.fetchFromDb(latitude, longitude)

    }
}