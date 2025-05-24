package com.android.app.weather.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.app.weather.AppExecutors
import com.android.app.weather.api.WeatherApi
import com.android.app.weather.api.WeatherServerResponse
import com.android.app.weather.db.WeatherDb
import com.android.app.weather.embedded.*
import com.android.app.weather.entities.WeatherUser
import com.android.app.weather.logger.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class UserRepository(val context: Context) {

    // LiveData holding the weather information, observed by WeatherViewModel.
    val weatherInDB = MutableLiveData<WeatherUser>()

    /**
     * Fetches weather data. It first attempts to retrieve data from the local database.
     * If data is not found in the database, it triggers a network request.
     *
     * @param latitude The latitude for which to fetch weather data.
     * @param longitude The longitude for which to fetch weather data.
     */
    fun fetchFromDb(latitude: String, longitude: String) {
        // Perform database operation on a background thread.
        AppExecutors.getInstance().diskIO().execute() {
            try {
                // Attempt to get weather information from the local database.
                val weatherRecord = WeatherDb.getInstance(context).dao.getWeatherInfo(latitude, longitude)
                if (weatherRecord == null) {
                    // Data not found in DB, log and fetch from network.
                    Logger.info("Weather data for lat: $latitude, lon: $longitude not found in DB. Fetching from network.")
                    fetchFromNetwork(latitude, longitude)
                } else {
                    // Data found in DB, log and post to LiveData.
                    Logger.info("Weather data for lat: $latitude, lon: $longitude fetched from DB.")
                    weatherInDB.postValue(weatherRecord)
                }
            } catch (e: Exception) {
                Logger.error("UserRepository: Exception during fetchFromDb (getWeatherInfo) for lat: $latitude, lon: $longitude: ${e.message}", e)
                weatherInDB.postValue(null) // Post null if DB read fails
            }
        }
    }

    /**
     * Fetches weather data from the OpenWeatherMap API.
     * If successful, it maps the response to a WeatherUser entity, stores it in the database,
     * and updates the weatherInDB LiveData by fetching the newly stored record.
     *
     * @param latitude The latitude for the API request.
     * @param longitude The longitude for the API request.
     */
    fun fetchFromNetwork(latitude: String, longitude: String) {
        // Setup Retrofit for network call
        val retrofit: Retrofit = Retrofit.Builder().client(createOkHttpClient())
            .baseUrl("https://api.openweathermap.org").addConverterFactory(
                SimpleXmlConverterFactory.createNonStrict(
                    Persister(AnnotationStrategy())
                )
            ).build();

        val weatherService: WeatherApi = retrofit.create(WeatherApi::class.java);



        val call: Call<WeatherServerResponse> =
            weatherService.getWeatherInfo(
                latitude,
                longitude,
                "5ad7218f2e11df834b0eaf3a33a39d2a",
                "xml"
            );

        AppExecutors.getInstance().networkIO().execute() {
            try {
                val serverResponse: retrofit2.Response<WeatherServerResponse>? = call.execute()
                Logger.info("Network response received for lat: $latitude, lon: $longitude.")
                if (serverResponse != null && serverResponse.isSuccessful) {
                    Logger.info("Network response successful.")
                    val responseBodyCurrent: WeatherServerResponse? = serverResponse.body()
                    if (responseBodyCurrent != null) {
                        Logger.info("Response body is not null. Mapping and storing data for city: ${responseBodyCurrent.city?.mName}")
                        val weatherUserFromNetwork = mapResponseToDbColumn(responseBodyCurrent)
                        try {
                            WeatherDb.getInstance(context).dao.insert(weatherUserFromNetwork)
                            Logger.info("Successfully inserted network data into DB for lat: $latitude, lon: $longitude.")
                            // Post the newly fetched and stored data to LiveData by re-querying the database.
                            try {
                                val updatedWeatherRecord = WeatherDb.getInstance(context).dao.getWeatherInfo(latitude, longitude)
                                weatherInDB.postValue(updatedWeatherRecord)
                            } catch (e: Exception) {
                                Logger.error("UserRepository: Exception during getWeatherInfo after insert for lat: $latitude, lon: $longitude: ${e.message}", e)
                                weatherInDB.postValue(null)
                            }
                        } catch (e: Exception) {
                            Logger.error("UserRepository: Exception during DB insert for lat: $latitude, lon: $longitude: ${e.message}", e)
                            weatherInDB.postValue(null) // Post null if DB insert fails
                        }
                    } else {
                        Logger.warn("Network response body is null for lat: $latitude, lon: $longitude.")
                        weatherInDB.postValue(null) // Post null if response body is null
                    }
                } else {
                    val errorBody = serverResponse?.errorBody()?.string() ?: "Unknown error"
                    Logger.error("Network request failed for lat: $latitude, lon: $longitude. Code: ${serverResponse?.code()}, Message: ${serverResponse?.message()}, ErrorBody: $errorBody")
                    weatherInDB.postValue(null) // Post null if network request itself fails
                }
            } catch (e: Exception) {
                Logger.error("UserRepository: Exception during network request execution for lat: $latitude, lon: $longitude: ${e.message}", e)
                weatherInDB.postValue(null) // Post null if network call execution fails
            }
        }
    }

    fun createOkHttpClient(): OkHttpClient {

        return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .readTimeout(10000, TimeUnit.MILLISECONDS).build();
    }

    private val httpLoggingInterceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()

        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    fun mapResponseToDbColumn(serverResponseBody: WeatherServerResponse?): WeatherUser {
        val city =
            City(
                serverResponseBody?.city?.mId,
                serverResponseBody?.city?.mName,
                serverResponseBody?.city?.mCord?.mLongitude,
                serverResponseBody?.city?.mCord?.mLatitude,
                serverResponseBody?.city?.mCountry,
                serverResponseBody?.city?.mTimeZone,
                serverResponseBody?.mCity?.mSun?.mSunRise,
                serverResponseBody?.mCity?.mSun?.mSunSet
            )
        val clouds =
            Clouds(
                serverResponseBody?.mCloud?.mName,
                serverResponseBody?.mCloud?.mValue
            )
        val feelsLike =
            FeelsLike(
                serverResponseBody?.mFeelsLike?.mValue,
                serverResponseBody?.mFeelsLike?.mUnit
            )
        val humidity =
            Humidity(
                serverResponseBody?.mHumidity?.mValue,
                serverResponseBody?.mHumidity?.mUnit
            )
        val lastUpdate =
            LastUpdate(serverResponseBody?.mLastUpdate?.mValue)
        val precipitation: Precipitation =
            Precipitation(serverResponseBody?.mPrecipitation?.mMode)
        val pressure =
            Pressure(
                serverResponseBody?.mPressure?.mValue,
                serverResponseBody?.mPressure?.mUnit
            )
        val temperature =
            Temperature(
                serverResponseBody?.mTemperature?.mCurrentTemp,
                serverResponseBody?.mTemperature?.mMaxTemp,
                serverResponseBody?.mTemperature?.mMinTemp
                ,
                serverResponseBody?.mTemperature?.mUnit
            )
        val visiblity =
            Visibility((serverResponseBody?.mVisibility?.mValue))
        val wind =
            Wind(
                serverResponseBody?.mWind?.mSpeed?.mValue,
                serverResponseBody?.mWind?.mSpeed?.mUnit,
                serverResponseBody?.mWind?.mSpeed?.mName,
                serverResponseBody?.mWind?.mGust?.mValue,
                serverResponseBody?.mWind?.mGust?.mUnit,
                serverResponseBody?.mWind?.mDirection?.mValue,
                serverResponseBody?.mWind?.mDirection?.mCode,
                serverResponseBody?.mWind?.mDirection?.mName
            )
        val weather =
            Weather(
                serverResponseBody?.mWeather?.mNumber,
                serverResponseBody?.mWeather?.mValue,
                serverResponseBody?.mWeather?.mIconId
            )
        return WeatherUser(
            city,
            temperature,
            feelsLike,
            humidity,
            pressure,
            clouds,
            visiblity,
            precipitation,
            wind,
            weather,
            lastUpdate
        );
    }
}