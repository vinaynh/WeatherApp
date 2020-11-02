package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

public class Weather {

    @ColumnInfo(name = "weather_number")
    public String mWeatherNumber;

    @ColumnInfo(name = "weather_value")
    public String mWeatherValue;

    @ColumnInfo(name = "weather_icon")
    public String mIconId;

    public Weather(String weatherNumber, String weatherValue, String iconId) {
        this.mWeatherNumber = weatherNumber;
        this.mWeatherValue = weatherValue;
        this.mIconId = iconId;
    }
}
