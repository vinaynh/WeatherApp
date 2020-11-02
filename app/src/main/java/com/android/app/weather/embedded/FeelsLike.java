package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

// FeelsLike class will be @embedded in WeatherUser
public class FeelsLike {

    @ColumnInfo(name = "feelslike_value")
    public String mFeelsLikeValue;

    @ColumnInfo(name = "feelslike_unit")
    public String mFeelsLikeUnit;


    public FeelsLike(String feelsLikeValue, String feelsLikeUnit) {
        this.mFeelsLikeValue = feelsLikeValue;
        this.mFeelsLikeUnit = feelsLikeUnit;
    }

}
