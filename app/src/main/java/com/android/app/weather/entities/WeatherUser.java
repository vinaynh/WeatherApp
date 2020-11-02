package com.android.app.weather.entities;


import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.app.weather.embedded.City;
import com.android.app.weather.embedded.Clouds;
import com.android.app.weather.embedded.FeelsLike;
import com.android.app.weather.embedded.Humidity;
import com.android.app.weather.embedded.LastUpdate;
import com.android.app.weather.embedded.Precipitation;
import com.android.app.weather.embedded.Pressure;
import com.android.app.weather.embedded.Temperature;
import com.android.app.weather.embedded.Visibility;
import com.android.app.weather.embedded.Weather;
import com.android.app.weather.embedded.Wind;

@Entity(tableName = "weather", primaryKeys = {"lat","lon"})
public class WeatherUser {

    @NonNull
    public String lat;

    @NonNull
    public String lon;

    @Embedded
    public City city;

    @Embedded
    public Temperature temperature;

    @Embedded
    public FeelsLike feelsLike;

    @Embedded
    public Humidity humidity;

    @Embedded
    public Pressure pressure;

    @Embedded
    public Clouds clouds;

    @Embedded
    public Visibility visibility;

    @Embedded
    public Precipitation precipitation;

    @Embedded
    public Wind wind;

    @Embedded
    public Weather weather;

    @Embedded
    public LastUpdate lastUpdate;


    public WeatherUser(City city, Temperature temperature, FeelsLike feelsLike, Humidity humidity, Pressure pressure, Clouds clouds, Visibility visibility,
                       Precipitation precipitation, Wind wind, Weather weather, LastUpdate lastUpdate) {
        this.city = city;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.pressure = pressure;
        this.clouds = clouds;
        this.visibility = visibility;
        this.precipitation = precipitation;
        this.wind = wind;
        this.weather = weather;
        this.lastUpdate = lastUpdate;
        this.lat = this.city.mLatitude;
        this.lon = this.city.mLongitude;

    }

}
