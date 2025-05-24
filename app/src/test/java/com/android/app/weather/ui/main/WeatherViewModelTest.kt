package com.android.app.weather.ui.main

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.android.app.weather.AppExecutors
import com.android.app.weather.api.WeatherApi
import com.android.app.weather.api.WeatherServerResponse
import com.android.app.weather.db.WeatherDao
import com.android.app.weather.db.WeatherDb
import com.android.app.weather.embedded.City
import com.android.app.weather.entities.WeatherUser
import com.android.app.weather.logger.Logger
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class WeatherViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApplication: Application
    @Mock
    private lateinit var mockContext: Context
    @Mock
    private lateinit var mockWeatherDao: WeatherDao
    @Mock
    private lateinit var mockWeatherApi: WeatherApi
    @Mock
    private lateinit var mockRetrofitCall: Call<WeatherServerResponse>
    @Mock
    private lateinit var mockObserver: Observer<WeatherUser?>

    private lateinit var viewModel: WeatherViewModel

    private lateinit var mockedWeatherDbStatic: org.mockito.MockedStatic<WeatherDb>
    private lateinit var mockedAppExecutorsStatic: org.mockito.MockedStatic<AppExecutors>
    private lateinit var mockedRetrofitBuilderStatic: org.mockito.MockedStatic<Retrofit.Builder>
    private lateinit var mockedLoggerStatic: org.mockito.MockedStatic<Logger>


    private val testLat = "10.0"
    private val testLon = "20.0"
    private val dummyWeatherUser = WeatherUser(
        city = City(id = 1, name = "Test City", lon = testLon, lat = testLat, country = "TC", timezone = 0, sunRise = 0, sunSet = 0),
        temperature = null, feelsLike = null, humidity = null, pressure = null, clouds = null, visibility = null, precipitation = null, wind = null, weather = null, lastUpdate = null
    )
    private val dummyWeatherServerResponse = WeatherServerResponse().apply {
        city = WeatherServerResponse.CityData().apply {
            mId = 1; mName = "Test City"
            mCord = WeatherServerResponse.Coordinates().apply { mLongitude = testLon; mLatitude = testLat }
            mCountry = "TC"; mTimeZone = 0
            mSun = WeatherServerResponse.SunData().apply { mSunRise = 0; mSunSet = 0 }
        }
        mTemperature = WeatherServerResponse.TemperatureData()
        mFeelsLike = WeatherServerResponse.FeelsLikeData()
        mHumidity = WeatherServerResponse.HumidityData()
        mPressure = WeatherServerResponse.PressureData()
        mCloud = WeatherServerResponse.CloudData()
        mVisibility = WeatherServerResponse.VisibilityData()
        mPrecipitation = WeatherServerResponse.PrecipitationData()
        mWind = WeatherServerResponse.WindData().apply {
            mSpeed = WeatherServerResponse.SpeedData(); mGust = WeatherServerResponse.GustData(); mDirection = WeatherServerResponse.DirectionData()
        }
        mWeather = WeatherServerResponse.WeatherData()
        mLastUpdate = WeatherServerResponse.LastUpdateData()
    }

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        `when`(mockApplication.applicationContext).thenReturn(mockContext)

        mockedWeatherDbStatic = mockStatic(WeatherDb::class.java)
        mockedWeatherDbStatic.`when`<Any> { WeatherDb.getInstance(mockContext) }.thenReturn(mockWeatherDb)
        `when`(mockWeatherDb.dao).thenReturn(mockWeatherDao)

        val realAppExecutors = AppExecutors()
        mockedAppExecutorsStatic = mockStatic(AppExecutors::class.java)
        mockedAppExecutorsStatic.`when`<Any> { AppExecutors.getInstance() }.thenReturn(realAppExecutors)

        val mockRetrofitBuilder = mock(Retrofit.Builder::class.java)
        val mockRetrofit = mock(Retrofit::class.java)
        mockedRetrofitBuilderStatic = mockStatic(Retrofit.Builder::class.java)
        mockedRetrofitBuilderStatic.`when`<Any> { Retrofit.Builder() }.thenReturn(mockRetrofitBuilder)
        `when`(mockRetrofitBuilder.client(org.mockito.ArgumentMatchers.any(OkHttpClient::class.java))).thenReturn(mockRetrofitBuilder)
        `when`(mockRetrofitBuilder.baseUrl(anyString())).thenReturn(mockRetrofitBuilder)
        `when`(mockRetrofitBuilder.addConverterFactory(org.mockito.ArgumentMatchers.any(SimpleXmlConverterFactory::class.java))).thenReturn(mockRetrofitBuilder)
        `when`(mockRetrofitBuilder.build()).thenReturn(mockRetrofit)
        `when`(mockRetrofit.create(WeatherApi::class.java)).thenReturn(mockWeatherApi)
        
        mockedLoggerStatic = mockStatic(Logger::class.java)

        viewModel = WeatherViewModel(mockApplication)
        viewModel.mediatorLiveData.observeForever(mockObserver)
    }

    @After
    fun tearDown() {
        viewModel.mediatorLiveData.removeObserver(mockObserver)
        mockedWeatherDbStatic.close()
        mockedAppExecutorsStatic.close()
        mockedRetrofitBuilderStatic.close()
        mockedLoggerStatic.close()
    }

    @Test
    fun `setGeoCordinates triggers repository actions and LiveData updates on DB success`() {
        `when`(mockWeatherDao.getWeatherInfo(testLat, testLon)).thenReturn(dummyWeatherUser)

        viewModel.setGeoCordinates(testLat, testLon)
        // Thread.sleep(100) // Not reliable for testing async code

        verify(mockWeatherDao).getWeatherInfo(testLat, testLon) // Verify DAO interaction (part of what fetchFromDb does)
        verify(mockObserver).onChanged(dummyWeatherUser) // Verify LiveData update
        verify(mockWeatherApi, never()).getWeatherInfo(anyString(), anyString(), anyString(), anyString())
    }

    @Test
    fun `setGeoCordinates triggers repository actions and LiveData updates on network success`() {
        `when`(mockWeatherDao.getWeatherInfo(testLat, testLon))
            .thenReturn(null) // First call
            .thenReturn(dummyWeatherUser) // After insert

        `when`(mockWeatherApi.getWeatherInfo(eq(testLat), eq(testLon), anyString(), eq("xml")))
            .thenReturn(mockRetrofitCall)
        `when`(mockRetrofitCall.execute()).thenReturn(Response.success(dummyWeatherServerResponse))

        viewModel.setGeoCordinates(testLat, testLon)
        // Thread.sleep(200) // Not reliable

        verify(mockWeatherDao, times(1)).getWeatherInfo(testLat, testLon) // Initial DAO check
        verify(mockWeatherApi).getWeatherInfo(eq(testLat), eq(testLon), anyString(), eq("xml")) // API call
        verify(mockWeatherDao).insert(org.mockito.ArgumentMatchers.any(WeatherUser::class.java)) // DAO insert
        verify(mockObserver).onChanged(dummyWeatherUser) // LiveData update
        verify(mockWeatherDao, times(2)).getWeatherInfo(testLat, testLon) // DAO check after insert
    }

    @Test
    fun `setGeoCordinates triggers repository actions and LiveData updates with null on network failure`() {
        `when`(mockWeatherDao.getWeatherInfo(testLat, testLon)).thenReturn(null)
        `when`(mockWeatherApi.getWeatherInfo(eq(testLat), eq(testLon), anyString(), eq("xml")))
            .thenReturn(mockRetrofitCall)
        val errorBody = okhttp3.ResponseBody.create(null, "Error")
        `when`(mockRetrofitCall.execute()).thenReturn(Response.error(404, errorBody))

        viewModel.setGeoCordinates(testLat, testLon)
        // Thread.sleep(100) // Not reliable

        verify(mockWeatherDao, times(1)).getWeatherInfo(testLat, testLon)
        verify(mockWeatherApi).getWeatherInfo(eq(testLat), eq(testLon), anyString(), eq("xml"))
        verify(mockWeatherDao, never()).insert(org.mockito.ArgumentMatchers.any(WeatherUser::class.java))
        verify(mockObserver).onChanged(null)
    }
    
    @Test
    fun `setGeoCordinates triggers repository actions and LiveData updates with null on DB error`() {
        `when`(mockWeatherDao.getWeatherInfo(testLat, testLon)).thenThrow(RuntimeException("DB error!"))

        viewModel.setGeoCordinates(testLat, testLon)
        // Thread.sleep(100) // Not reliable

        verify(mockWeatherDao).getWeatherInfo(testLat, testLon)
        verify(mockObserver).onChanged(null)
        verify(mockWeatherApi, never()).getWeatherInfo(anyString(), anyString(), anyString(), anyString())
    }
}
