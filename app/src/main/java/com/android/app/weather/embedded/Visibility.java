package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

// Visibility class will be @embedded in WeatherUser
public class Visibility {

    @ColumnInfo(name = "visibility_value")
    public String mVisiblityValue;

    public Visibility(String visiblityValue) {
        this.mVisiblityValue = visiblityValue;
    }
}
