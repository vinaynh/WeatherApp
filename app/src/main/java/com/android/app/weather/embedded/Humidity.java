package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

// Humidity class will be @embedded in WeatherUser
public class Humidity {

    @ColumnInfo(name = "humidity_value")
    public String mHumidityValue;

    @ColumnInfo(name = "humidity_unit")
    public String mHumidityUnit;

    public Humidity(String humidityValue, String humidityUnit) {
        this.mHumidityValue = humidityValue;
        this.mHumidityUnit = humidityUnit;
    }
}
