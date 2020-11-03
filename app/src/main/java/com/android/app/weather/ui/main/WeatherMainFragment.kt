package com.android.app.weather.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.app.weather.R
import com.android.app.weather.entities.WeatherUser
import com.android.app.weather.logger.Logger

class WeatherMainFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherMainFragment()
    }

    private lateinit var viewModel: WeatherViewModel

    private var latitude: String = ""
    private var longitude: String = ""

    private lateinit var cityNameTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var latitudeTextView: TextView
    private lateinit var sunriseTextView: TextView
    private lateinit var sunsetTextView: TextView
    private lateinit var currentTempTextView: TextView
    private lateinit var maxTempTextView: TextView
    private lateinit var minTempTextView: TextView
    private lateinit var feelsLikeTempTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var precipitationTextView: TextView
    private lateinit var visibilityTextView: TextView
    private lateinit var cloudForecastTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var directionTextView: TextView
    private lateinit var WeatherTextView: TextView
    private lateinit var lastUpdateTextView: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        cityNameTextView = view.findViewById(R.id.cityname);
        countryTextView = view.findViewById(R.id.citycountry)
        longitudeTextView = view.findViewById(R.id.citylongitude)
        latitudeTextView = view.findViewById(R.id.citylatitude)
        sunriseTextView = view.findViewById(R.id.citysunrise)
        sunsetTextView = view.findViewById(R.id.citysunSet)
        currentTempTextView = view.findViewById(R.id.currenttemp)
        maxTempTextView = view.findViewById(R.id.maxtemp)
        minTempTextView = view.findViewById(R.id.mintemp)
        minTempTextView = view.findViewById(R.id.mintemp)
        feelsLikeTempTextView = view.findViewById(R.id.feelsliketemp)
        pressureTextView = view.findViewById(R.id.pressure)
        humidityTextView = view.findViewById(R.id.humidity)
        precipitationTextView = view.findViewById(R.id.percipitation)
        visibilityTextView = view.findViewById(R.id.visibility)
        cloudForecastTextView = view.findViewById(R.id.cloudforecast)
        windSpeedTextView = view.findViewById(R.id.windspeed)
        directionTextView = view.findViewById(R.id.direction)
        WeatherTextView = view.findViewById(R.id.weather)
        lastUpdateTextView = view.findViewById(R.id.lastUpdate)
        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(WeatherViewModel::class.java)
        val arguments: Bundle? = this.arguments

        if (arguments != null) {
            latitude = arguments.getString("latitude", "")
            longitude = arguments.getString("longitude", "")
        }

        if (latitude == null || longitude == null) {
            Logger.info("latitude and longitude is null in fragmnent")
            requireActivity().finish()
            return
        }

        viewModel.setGeoCordinates(latitude, longitude);


        // Create the observer which updates the UI.
        val nameObserver = Observer<WeatherUser> { WeatherTable ->

            cityNameTextView.text = cityNameTextView.text.toString().plus(":").plus(WeatherTable.city.mCityName)
            countryTextView.text = countryTextView.text.toString().plus(":").plus(WeatherTable.city.mCountry)
            longitudeTextView.text = longitudeTextView.text.toString().plus(":").plus(WeatherTable.lon)
            latitudeTextView.text = latitudeTextView.text.toString().plus(":").plus(WeatherTable.lat)
            sunriseTextView.text = sunriseTextView.text.toString().plus(":").plus(WeatherTable.city.mSunRise)
            sunsetTextView.text = sunsetTextView.text.toString().plus(":").plus(WeatherTable.city.mSunSet)
            currentTempTextView.text = currentTempTextView.text.toString().plus(":").plus(WeatherTable.temperature.mCurrentTemp)
            minTempTextView.text = minTempTextView.text.toString().plus(":").plus(WeatherTable.temperature.mMinTemp)
            maxTempTextView.text = maxTempTextView.text.toString().plus(":").plus(WeatherTable.temperature.mMaxTemp)
            feelsLikeTempTextView.text = feelsLikeTempTextView.text.toString().plus(":").plus(WeatherTable.feelsLike.mFeelsLikeValue)
            pressureTextView.text = pressureTextView.text.toString().plus(":").plus(WeatherTable.pressure.mValue)

            humidityTextView.text = humidityTextView.text.toString().plus(":").plus(WeatherTable.humidity.mHumidityValue)
            precipitationTextView.text = precipitationTextView.text.toString().plus(":").plus(WeatherTable.precipitation.mPrecipitationMode)
            visibilityTextView.text = visibilityTextView.text.toString().plus(":").plus(WeatherTable.visibility.mVisiblityValue)
            cloudForecastTextView.text = cloudForecastTextView.text.toString().plus(":").plus(WeatherTable.clouds.mCloudsValue)
            windSpeedTextView.text = windSpeedTextView.text.toString().plus(":").plus(WeatherTable.wind.mSpeedValue)
            directionTextView.text = directionTextView.text.toString().plus(":").plus(WeatherTable.wind.mDirectionValue)
            WeatherTextView.text = WeatherTextView.text.toString().plus(":").plus(WeatherTable.weather.mWeatherValue)
            lastUpdateTextView.text = lastUpdateTextView.text.toString().plus(":").plus(WeatherTable.lastUpdate.mLastUpdateValue)

        }

        viewModel.mediatorLiveData.observe(this, nameObserver)

    }
}
