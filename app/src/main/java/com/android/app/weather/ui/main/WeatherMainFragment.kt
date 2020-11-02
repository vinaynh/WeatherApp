package com.android.app.weather.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.android.app.weather.R
import com.android.app.weather.entities.*
import com.android.app.weather.logger.Logger

class WeatherMainFragment : Fragment() {

    companion object {
        fun newInstance() = WeatherMainFragment()
    }

    private lateinit var viewModel: WeatherViewModel

    private   var latitude :String = ""
    private   var longitude :String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)


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
            Logger.info(" "+ WeatherTable.city.mCityId);
        }

        viewModel.weather.observe(this, nameObserver)
        // TODO: Use the ViewModel

    }
}
