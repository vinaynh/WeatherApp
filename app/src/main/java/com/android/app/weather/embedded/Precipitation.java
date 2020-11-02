package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

// Precipitation class will be @embedded in WeatherUser
public class Precipitation {

    @ColumnInfo(name = "precipitation_mode")
    public String mPrecipitationMode;

    public Precipitation(String mPrecipitationMode) {
        this.mPrecipitationMode = mPrecipitationMode;
    }
}
