package com.android.app.weather.embedded;

import androidx.room.ColumnInfo;

// Temperature class  will be @embedded in WeatherUser
public class Temperature {

    @ColumnInfo(name = "Temperature_value")
    public String mCurrentTemp;

    @ColumnInfo(name = "Temperature_min")
    public String mMaxTemp;

    @ColumnInfo(name = "Temperature_max")
    public String mMinTemp;

    @ColumnInfo(name = "Temperature_unit")
    public String mTempUnit;

    public Temperature(String mCurrentTemp, String mMaxTemp, String mMinTemp, String mTempUnit) {
        this.mCurrentTemp = mCurrentTemp;
        this.mMaxTemp = mMaxTemp;
        this.mMinTemp = mMinTemp;
        this.mTempUnit = mTempUnit;
    }
}
