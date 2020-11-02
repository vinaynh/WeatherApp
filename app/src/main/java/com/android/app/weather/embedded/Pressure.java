package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

// Pressure class will be @embedded in WeatherUser
public class Pressure {

    @ColumnInfo(name = "pressure_value")
    public String mValue;

    @ColumnInfo(name = "pressure_unit")
    public String mPressureUnit;

    public Pressure(String value, String pressureUnit) {
        this.mValue = value;
        this.mPressureUnit = pressureUnit;
    }
}
