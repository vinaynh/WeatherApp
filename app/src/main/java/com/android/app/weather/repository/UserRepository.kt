package com.android.app.weather.repository

import android.content.Context
import android.util.Log
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

    fun fetchFromDb(latitude: String, longitude: String) {
        Logger.info("fetchFromDV")

        AppExecutors.getInstance().diskIO().execute() {
            val weatherTableList = WeatherDb.getInstance(context).dao.getWeatherInfo("%".plus(latitude).plus("%"), "%".plus(longitude).plus("%"))
            if (weatherTableList == null) {
                Logger.info("weather is null")
                fetchFromNetwork(latitude, longitude);
            }
        }
    }


    fun fetchFromNetwork(latitude: String, longitude: String) {
        val retrofit: Retrofit = Retrofit.Builder().client(createOkHttpClient())
            .baseUrl("https://api.openweathermap.org").addConverterFactory(
                SimpleXmlConverterFactory.createNonStrict(
                    Persister(AnnotationStrategy())
                )
            ).build();

        val weatherService: WeatherApi = retrofit.create(WeatherApi::class.java);
        Logger.info("user repository latitude in ==="+latitude)
        Logger.info("user repository latitude==="+longitude)


        val call: Call<WeatherServerResponse> =
            weatherService.getWeatherInfo(
                latitude,
                longitude,
                "5ad7218f2e11df834b0eaf3a33a39d2a",
                "xml"
            );

        AppExecutors.getInstance().networkIO().execute() {
            val serverResponse: retrofit2.Response<WeatherServerResponse>? = call.execute();
            Logger.info("server response")
            if (serverResponse != null) {
                if (serverResponse.isSuccessful()) {
                    Logger.info("response is successfull")
                    val responseBodyCurrent: WeatherServerResponse? = serverResponse.body();
                    if (responseBodyCurrent != null) {
                        Log.i("okhttp", "city weather insertion" + responseBodyCurrent.city.mName)
                        WeatherDb.getInstance(context).dao.insert(
                            mapResponseToDbColumn(
                                responseBodyCurrent
                            )
                        )

                    }
                }
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
        val city: City =
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
        val clouds: Clouds =
            Clouds(
                serverResponseBody?.mCloud?.mName,
                serverResponseBody?.mCloud?.mValue
            )
        val feelsLike: FeelsLike =
            FeelsLike(
                serverResponseBody?.mFeelsLike?.mValue,
                serverResponseBody?.mFeelsLike?.mUnit
            )
        val humidity: Humidity =
            Humidity(
                serverResponseBody?.mHumidity?.mValue,
                serverResponseBody?.mHumidity?.mUnit
            )
        val lastUpdate: LastUpdate =
            LastUpdate(serverResponseBody?.mLastUpdate?.mValue)
        val precipitation: Precipitation =
            Precipitation(serverResponseBody?.mPrecipitation?.mMode)
        val pressure: Pressure =
            Pressure(
                serverResponseBody?.mPressure?.mValue,
                serverResponseBody?.mPressure?.mUnit
            )
        val temperature: Temperature =
            Temperature(
                serverResponseBody?.mTemperature?.mCurrentTemp,
                serverResponseBody?.mTemperature?.mMaxTemp,
                serverResponseBody?.mTemperature?.mMinTemp
                ,
                serverResponseBody?.mTemperature?.mUnit
            )
        val visiblity: Visibility =
            Visibility((serverResponseBody?.mVisibility?.mValue))
        val wind: Wind =
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
        val weather: Weather =
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