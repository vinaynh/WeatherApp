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
        minTempTextView = view.findViewById(R.id.mintemp) // Duplicate removed
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
        // Obtain WeatherViewModel scoped to the WeatherActivity's lifecycle.
        // This ensures the ViewModel survives fragment recreations and shares data across fragments if needed.
        viewModel = ViewModelProvider(
                requireActivity(), // Use requireActivity() as the ViewModelStoreOwner
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(WeatherViewModel::class.java)
        val arguments: Bundle? = this.arguments

        if (arguments != null) {
            latitude = arguments.getString("latitude", "")
            longitude = arguments.getString("longitude", "")
        }

        // Retrieve latitude and longitude from arguments.
        // WeatherActivity is expected to always provide these.
        // If they are somehow null (which shouldn't happen with current WeatherActivity logic),
        // log the error and finish the activity.
        if (latitude == null || longitude == null) {
            Logger.info("Latitude and/or longitude arguments are null in WeatherMainFragment.")
            requireActivity().finish()
            return
        }

        // Pass latitude and longitude to the ViewModel.
        // The ViewModel will then be responsible for fetching and preparing weather data.
        viewModel.setGeoCordinates(latitude, longitude);


        // Create the observer which updates the UI with weather data from the ViewModel.
        val nameObserver = Observer<WeatherUser?> { weatherUser ->
            if (weatherUser == null) {
                Logger.warn("WeatherMainFragment: Received null WeatherUser data. Displaying unavailable state.")
                // Display error or unavailable state
                cityNameTextView.text = getString(R.string.weather_data_unavailable)
                countryTextView.text = getString(R.string.country_na)
                longitudeTextView.text = getString(R.string.longitude_na)
                latitudeTextView.text = getString(R.string.latitude_na)
                sunriseTextView.text = getString(R.string.sunrise_na)
                sunsetTextView.text = getString(R.string.sunset_na)
                currentTempTextView.text = getString(R.string.current_temp_na)
                maxTempTextView.text = getString(R.string.max_temp_na)
                minTempTextView.text = getString(R.string.min_temp_na)
                feelsLikeTempTextView.text = getString(R.string.feels_like_na)
                pressureTextView.text = getString(R.string.pressure_na)
                humidityTextView.text = getString(R.string.humidity_na)
                precipitationTextView.text = getString(R.string.precipitation_na)
                visibilityTextView.text = getString(R.string.visibility_na)
                cloudForecastTextView.text = getString(R.string.cloud_forecast_na)
                windSpeedTextView.text = getString(R.string.wind_speed_na)
                directionTextView.text = getString(R.string.direction_na)
                WeatherTextView.text = getString(R.string.weather_na)
                lastUpdateTextView.text = getString(R.string.last_update_na)
            } else {
                Logger.info("WeatherMainFragment: Received WeatherUser data. Updating UI.")
                // Use R.string format specifiers for better localization and clarity
                cityNameTextView.text = getString(R.string.city_name_format, weatherUser.city?.mCityName ?: getString(R.string.na))
                countryTextView.text = getString(R.string.country_format, weatherUser.city?.mCountry ?: getString(R.string.na))
                longitudeTextView.text = getString(R.string.longitude_format, weatherUser.lon ?: getString(R.string.na))
                latitudeTextView.text = getString(R.string.latitude_format, weatherUser.lat ?: getString(R.string.na))
                sunriseTextView.text = getString(R.string.sunrise_format, weatherUser.city?.mSunRise ?: getString(R.string.na))
                sunsetTextView.text = getString(R.string.sunset_format, weatherUser.city?.mSunSet ?: getString(R.string.na))
                currentTempTextView.text = getString(R.string.current_temp_format, weatherUser.temperature?.mCurrentTemp ?: getString(R.string.na))
                minTempTextView.text = getString(R.string.min_temp_format, weatherUser.temperature?.mMinTemp ?: getString(R.string.na))
                maxTempTextView.text = getString(R.string.max_temp_format, weatherUser.temperature?.mMaxTemp ?: getString(R.string.na))
                feelsLikeTempTextView.text = getString(R.string.feels_like_format, weatherUser.feelsLike?.mFeelsLikeValue ?: getString(R.string.na))
                pressureTextView.text = getString(R.string.pressure_format, weatherUser.pressure?.mValue ?: getString(R.string.na))
                humidityTextView.text = getString(R.string.humidity_format, weatherUser.humidity?.mHumidityValue ?: getString(R.string.na))
                precipitationTextView.text = getString(R.string.precipitation_format, weatherUser.precipitation?.mPrecipitationMode ?: getString(R.string.na))
                visibilityTextView.text = getString(R.string.visibility_format, weatherUser.visibility?.mVisiblityValue ?: getString(R.string.na))
                cloudForecastTextView.text = getString(R.string.cloud_forecast_format, weatherUser.clouds?.mCloudsValue ?: getString(R.string.na))
                windSpeedTextView.text = getString(R.string.wind_speed_format, weatherUser.wind?.mSpeedValue ?: getString(R.string.na))
                directionTextView.text = getString(R.string.direction_format, weatherUser.wind?.mDirectionValue ?: getString(R.string.na))
                WeatherTextView.text = getString(R.string.weather_format, weatherUser.weather?.mWeatherValue ?: getString(R.string.na))
                lastUpdateTextView.text = getString(R.string.last_update_format, weatherUser.lastUpdate?.mLastUpdateValue ?: getString(R.string.na))
            }
        }

        // Observe the LiveData from the ViewModel.
        // When the data changes (e.g., weather information is fetched),
        // the 'nameObserver' will be triggered to update the UI.
        viewModel.mediatorLiveData.observe(viewLifecycleOwner, nameObserver)

    }
}
