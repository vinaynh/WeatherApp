package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

// Cloud class will be @embedded in WeatherUser
public class Clouds {

    @ColumnInfo(name = "clouds_value")
    public String mCloudsValue;

    @ColumnInfo(name = "clouds_name")
    public String mCloudsName;

    public Clouds(String cloudsValue, String cloudsName) {
        this.mCloudsValue = cloudsValue;
        this.mCloudsName = cloudsName;
    }
}
